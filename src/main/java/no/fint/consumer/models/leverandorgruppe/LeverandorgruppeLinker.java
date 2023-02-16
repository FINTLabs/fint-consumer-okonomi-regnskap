package no.fint.consumer.models.leverandorgruppe;

import no.fint.model.resource.okonomi.regnskap.LeverandorgruppeResource;
import no.fint.model.resource.okonomi.regnskap.LeverandorgruppeResources;
import no.fint.relations.FintLinker;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class LeverandorgruppeLinker extends FintLinker<LeverandorgruppeResource> {

    public LeverandorgruppeLinker() {
        super(LeverandorgruppeResource.class);
    }

    public void mapLinks(LeverandorgruppeResource resource) {
        super.mapLinks(resource);
    }

    @Override
    public LeverandorgruppeResources toResources(Collection<LeverandorgruppeResource> collection) {
        return toResources(collection.stream(), 0, 0, collection.size());
    }

    @Override
    public LeverandorgruppeResources toResources(Stream<LeverandorgruppeResource> stream, int offset, int size, int totalItems) {
        LeverandorgruppeResources resources = new LeverandorgruppeResources();
        stream.map(this::toResource).forEach(resources::addResource);
        addPagination(resources, offset, size, totalItems);
        return resources;
    }

    @Override
    public String getSelfHref(LeverandorgruppeResource leverandorgruppe) {
        return getAllSelfHrefs(leverandorgruppe).findFirst().orElse(null);
    }

    @Override
    public Stream<String> getAllSelfHrefs(LeverandorgruppeResource leverandorgruppe) {
        Stream.Builder<String> builder = Stream.builder();
        if (!isNull(leverandorgruppe.getSystemId()) && !isEmpty(leverandorgruppe.getSystemId().getIdentifikatorverdi())) {
            builder.add(createHrefWithId(leverandorgruppe.getSystemId().getIdentifikatorverdi(), "systemid"));
        }
        
        return builder.build();
    }

    int[] hashCodes(LeverandorgruppeResource leverandorgruppe) {
        IntStream.Builder builder = IntStream.builder();
        if (!isNull(leverandorgruppe.getSystemId()) && !isEmpty(leverandorgruppe.getSystemId().getIdentifikatorverdi())) {
            builder.add(leverandorgruppe.getSystemId().getIdentifikatorverdi().hashCode());
        }
        
        return builder.build().toArray();
    }

}

