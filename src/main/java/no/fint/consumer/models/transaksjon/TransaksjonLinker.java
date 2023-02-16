package no.fint.consumer.models.transaksjon;

import no.fint.model.resource.okonomi.regnskap.TransaksjonResource;
import no.fint.model.resource.okonomi.regnskap.TransaksjonResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class TransaksjonLinker extends FintLinker<TransaksjonResource> {

    public TransaksjonLinker() {
        super(TransaksjonResource.class);
    }

    public void mapLinks(TransaksjonResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public TransaksjonResources toResources(Collection<TransaksjonResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public TransaksjonResources toResources(Stream<TransaksjonResource> stream, int offset, int size, int totalItems) {
        TransaksjonResources resources = new TransaksjonResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(TransaksjonResource transaksjon) {
        return getAllSelfHrefs(transaksjon).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(TransaksjonResource transaksjon) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(transaksjon.getTransaksjonsId()) && !isEmpty(transaksjon.getTransaksjonsId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(transaksjon.getTransaksjonsId().getIdentifikatorverdi(), "transaksjonsid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(TransaksjonResource transaksjon) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(transaksjon.getTransaksjonsId()) && !isEmpty(transaksjon.getTransaksjonsId().getIdentifikatorverdi())) {
            builder.add(transaksjon.getTransaksjonsId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

