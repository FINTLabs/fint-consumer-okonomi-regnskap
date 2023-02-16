package no.fint.consumer.models.transaksjon;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

import no.fint.cache.CacheService;
import no.fint.cache.model.CacheObject;
import no.fint.consumer.config.Constants;
import no.fint.consumer.config.ConsumerProps;
import no.fint.consumer.event.ConsumerEventUtil;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.relations.FintResourceCompatibility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.fint.model.okonomi.regnskap.Transaksjon;
import no.fint.model.resource.okonomi.regnskap.TransaksjonResource;
import no.fint.model.okonomi.regnskap.RegnskapActions;
import no.fint.model.felles.kompleksedatatyper.Identifikator;

@Slf4j
@Service
@ConditionalOnProperty(name = "fint.consumer.cache.disabled.transaksjon", havingValue = "false", matchIfMissing = true)
public class TransaksjonCacheService extends CacheService<TransaksjonResource> {

    public static final String MODEL = Transaksjon.class.getSimpleName().toLowerCase();

    @Value("${fint.consumer.compatibility.fintresource:true}")
    private boolean checkFintResourceCompatibility;

    @Autowired
    private FintResourceCompatibility fintResourceCompatibility;

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @Autowired
    private ConsumerProps props;

    @Autowired
    private TransaksjonLinker linker;

    private JavaType javaType;

    private ObjectMapper objectMapper;

    public TransaksjonCacheService() {
        super(MODEL, RegnskapActions.GET_ALL_TRANSAKSJON, RegnskapActions.UPDATE_TRANSAKSJON);
        objectMapper = new ObjectMapper();
        javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, TransaksjonResource.class);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @PostConstruct
    public void init() {
        props.getAssets().forEach(this::createCache);
    }

    @Scheduled(initialDelayString = Constants.CACHE_INITIALDELAY_TRANSAKSJON, fixedRateString = Constants.CACHE_FIXEDRATE_TRANSAKSJON)
    public void populateCacheAll() {
        props.getAssets().forEach(this::populateCache);
    }

    public void rebuildCache(String orgId) {
		flush(orgId);
		populateCache(orgId);
	}

    @Override
    public void populateCache(String orgId) {
		log.info("Populating Transaksjon cache for {}", orgId);
        Event event = new Event(orgId, Constants.COMPONENT, RegnskapActions.GET_ALL_TRANSAKSJON, Constants.CACHE_SERVICE);
        consumerEventUtil.send(event);
    }


    public Optional<TransaksjonResource> getTransaksjonByTransaksjonsId(String orgId, String transaksjonsId) {
        return getOne(orgId, transaksjonsId.hashCode(),
            (resource) -> Optional
                .ofNullable(resource)
                .map(TransaksjonResource::getTransaksjonsId)
                .map(Identifikator::getIdentifikatorverdi)
                .map(transaksjonsId::equals)
                .orElse(false));
    }


	@Override
    public void onAction(Event event) {
        List<TransaksjonResource> data;
        if (checkFintResourceCompatibility && fintResourceCompatibility.isFintResourceData(event.getData())) {
            log.info("Compatibility: Converting FintResource<TransaksjonResource> to TransaksjonResource ...");
            data = fintResourceCompatibility.convertResourceData(event.getData(), TransaksjonResource.class);
        } else {
            data = objectMapper.convertValue(event.getData(), javaType);
        }
        data.forEach(linker::mapLinks);
        if (RegnskapActions.valueOf(event.getAction()) == RegnskapActions.UPDATE_TRANSAKSJON) {
            if (event.getResponseStatus() == ResponseStatus.ACCEPTED || event.getResponseStatus() == ResponseStatus.CONFLICT) {
                List<CacheObject<TransaksjonResource>> cacheObjects = data
                    .stream()
                    .map(i -> new CacheObject<>(i, linker.hashCodes(i)))
                    .collect(Collectors.toList());
                addCache(event.getOrgId(), cacheObjects);
                log.info("Added {} cache objects to cache for {}", cacheObjects.size(), event.getOrgId());
            } else {
                log.debug("Ignoring payload for {} with response status {}", event.getOrgId(), event.getResponseStatus());
            }
        } else {
            List<CacheObject<TransaksjonResource>> cacheObjects = data
                    .stream()
                    .map(i -> new CacheObject<>(i, linker.hashCodes(i)))
                    .collect(Collectors.toList());
            updateCache(event.getOrgId(), cacheObjects);
            log.info("Updated cache for {} with {} cache objects", event.getOrgId(), cacheObjects.size());
        }
    }
}
