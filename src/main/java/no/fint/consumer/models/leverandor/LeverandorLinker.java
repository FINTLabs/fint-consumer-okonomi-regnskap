package no.fint.consumer.models.leverandor;

import no.fint.model.resource.okonomi.regnskap.LeverandorResource;
import no.fint.model.resource.okonomi.regnskap.LeverandorResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class LeverandorLinker extends FintLinker<LeverandorResource> {

    public LeverandorLinker() {
        super(LeverandorResource.class);
    }

    public void mapLinks(LeverandorResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public LeverandorResources toResources(Collection<LeverandorResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public LeverandorResources toResources(Stream<LeverandorResource> stream, int offset, int size, int totalItems) {
        LeverandorResources resources = new LeverandorResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(LeverandorResource leverandor) {
        return getAllSelfHrefs(leverandor).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(LeverandorResource leverandor) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(leverandor.getLeverandornummer()) && !isEmpty(leverandor.getLeverandornummer().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(leverandor.getLeverandornummer().getIdentifikatorverdi(), "leverandornummer"));
        }
        if (!isNull(leverandor.getSystemId()) && !isEmpty(leverandor.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(leverandor.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(LeverandorResource leverandor) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(leverandor.getLeverandornummer()) && !isEmpty(leverandor.getLeverandornummer().getIdentifikatorverdi())) {
            builder.add(leverandor.getLeverandornummer().getIdentifikatorverdi().hashCode());
        }
        if (!isNull(leverandor.getSystemId()) && !isEmpty(leverandor.getSystemId().getIdentifikatorverdi())) {
            builder.add(leverandor.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

