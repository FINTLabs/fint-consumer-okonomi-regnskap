package no.fint.consumer.config;

import no.fint.consumer.utils.RestEndpoints;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import no.fint.model.okonomi.regnskap.Leverandor;
import no.fint.model.okonomi.regnskap.Leverandorgruppe;
import no.fint.model.okonomi.regnskap.Postering;
import no.fint.model.okonomi.regnskap.Transaksjon;

public class LinkMapper {

    public static Map<String, String> linkMapper(String contextPath) {
        return ImmutableMap.<String,String>builder()
            .put(Leverandor.class.getName(), contextPath + RestEndpoints.LEVERANDOR)
            .put(Leverandorgruppe.class.getName(), contextPath + RestEndpoints.LEVERANDORGRUPPE)
            .put(Postering.class.getName(), contextPath + RestEndpoints.POSTERING)
            .put(Transaksjon.class.getName(), contextPath + RestEndpoints.TRANSAKSJON)
            .put("no.fint.model.administrasjon.kodeverk.Aktivitet", "/administrasjon/kodeverk/aktivitet")
            .put("no.fint.model.administrasjon.kodeverk.Anlegg", "/administrasjon/kodeverk/anlegg")
            .put("no.fint.model.administrasjon.kodeverk.Ansvar", "/administrasjon/kodeverk/ansvar")
            .put("no.fint.model.administrasjon.kodeverk.Art", "/administrasjon/kodeverk/art")
            .put("no.fint.model.administrasjon.kodeverk.Diverse", "/administrasjon/kodeverk/diverse")
            .put("no.fint.model.administrasjon.kodeverk.Formal", "/administrasjon/kodeverk/formal")
            .put("no.fint.model.administrasjon.kodeverk.Funksjon", "/administrasjon/kodeverk/funksjon")
            .put("no.fint.model.administrasjon.kodeverk.Kontrakt", "/administrasjon/kodeverk/kontrakt")
            .put("no.fint.model.administrasjon.kodeverk.Lopenummer", "/administrasjon/kodeverk/lopenummer")
            .put("no.fint.model.administrasjon.kodeverk.Objekt", "/administrasjon/kodeverk/objekt")
            .put("no.fint.model.administrasjon.kodeverk.Prosjekt", "/administrasjon/kodeverk/prosjekt")
            .put("no.fint.model.administrasjon.kodeverk.Ramme", "/administrasjon/kodeverk/ramme")
            .put("no.fint.model.felles.Person", "/felles/person")
            .put("no.fint.model.felles.Virksomhet", "/felles/virksomhet")
            .put("no.fint.model.administrasjon.personal.Personalressurs", "/administrasjon/personal/personalressurs")
            .put("no.fint.model.felles.kodeverk.Valuta", "/felles/kodeverk/valuta")
            /* .put(TODO,TODO) */
            .build();
    }

}
