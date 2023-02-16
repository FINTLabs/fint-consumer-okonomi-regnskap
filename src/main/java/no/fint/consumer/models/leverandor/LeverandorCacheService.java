package no.fint.consumer.models.leverandor;

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

import no.fint.model.okonomi.regnskap.Leverandor;
import no.fint.model.resource.okonomi.regnskap.LeverandorResource;
import no.fint.model.okonomi.regnskap.RegnskapActions;
import no.fint.model.felles.kompleksedatatyper.Identifikator;

@Slf4j
@Service
@ConditionalOnProperty(name = "fint.consumer.cache.disabled.leverandor", havingValue = "false", matchIfMissing = true)
public class LeverandorCacheService extends CacheService<LeverandorResource> {

    public static final String MODEL = Leverandor.class.getSimpleName().toLowerCase();

    @Value("${fint.consumer.compatibility.fintresource:true}")
    private boolean checkFintResourceCompatibility;

    @Autowired
    private FintResourceCompatibility fintResourceCompatibility;

    @Autowired
    private ConsumerEventUtil consumerEventUtil;

    @Autowired
    private ConsumerProps props;

    @Autowired
    private LeverandorLinker linker;

    private JavaType javaType;

    private ObjectMapper objectMapper;

    public LeverandorCacheService() {
        super(MODEL, RegnskapActions.GET_ALL_LEVERANDOR, RegnskapActions.UPDATE_LEVERANDOR);
        objectMapper = new ObjectMapper();
        javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, LeverandorResource.class);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @PostConstruct
    public void init() {
        props.getAssets().forEach(this::createCache);
    }

    @Scheduled(initialDelayString = Constants.CACHE_INITIALDELAY_LEVERANDOR, fixedRateString = Constants.CACHE_FIXEDRATE_LEVERANDOR)
    public void populateCacheAll() {
        props.getAssets().forEach(this::populateCache);
    }

    public void rebuildCache(String orgId) {
		flush(orgId);
		populateCache(orgId);
	}

    @Override
    public void populateCache(String orgId) {
		log.info("Populating Leverandor cache for {}", orgId);
        Event event = new Event(orgId, Constants.COMPONENT, RegnskapActions.GET_ALL_LEVERANDOR, Constants.CACHE_SERVICE);
        consumerEventUtil.send(event);
    }


    public Optional<LeverandorResource> getLeverandorByLeverandornummer(String orgId, String leverandornummer) {
        return getOne(orgId, leverandornummer.hashCode(),
            (resource) -> Optional
                .ofNullable(resource)
                .map(LeverandorResource::getLeverandornummer)
                .map(Identifikator::getIdentifikatorverdi)
                .map(leverandornummer::equals)
                .orElse(false));
    }

    public Optional<LeverandorResource> getLeverandorBySystemId(String orgId, String systemId) {
        return getOne(orgId, systemId.hashCode(),
            (resource) -> Optional
                .ofNullable(resource)
                .map(LeverandorResource::getSystemId)
                .map(Identifikator::getIdentifikatorverdi)
                .map(systemId::equals)
                .orElse(false));
    }


	@Override
    public void onAction(Event event) {
        List<LeverandorResource> data;
        if (checkFintResourceCompatibility && fintResourceCompatibility.isFintResourceData(event.getData())) {
            log.info("Compatibility: Converting FintResource<LeverandorResource> to LeverandorResource ...");
            data = fintResourceCompatibility.convertResourceData(event.getData(), LeverandorResource.class);
        } else {
            data = objectMapper.convertValue(event.getData(), javaType);
        }
        data.forEach(linker::mapLinks);
        if (RegnskapActions.valueOf(event.getAction()) == RegnskapActions.UPDATE_LEVERANDOR) {
            if (event.getResponseStatus() == ResponseStatus.ACCEPTED || event.getResponseStatus() == ResponseStatus.CONFLICT) {
                List<CacheObject<LeverandorResource>> cacheObjects = data
                    .stream()
                    .map(i -> new CacheObject<>(i, linker.hashCodes(i)))
                    .collect(Collectors.toList());
                addCache(event.getOrgId(), cacheObjects);
                log.info("Added {} cache objects to cache for {}", cacheObjects.size(), event.getOrgId());
            } else {
                log.debug("Ignoring payload for {} with response status {}", event.getOrgId(), event.getResponseStatus());
            }
        } else {
            List<CacheObject<LeverandorResource>> cacheObjects = data
                    .stream()
                    .map(i -> new CacheObject<>(i, linker.hashCodes(i)))
                    .collect(Collectors.toList());
            updateCache(event.getOrgId(), cacheObjects);
            log.info("Updated cache for {} with {} cache objects", event.getOrgId(), cacheObjects.size());
        }
    }
}
