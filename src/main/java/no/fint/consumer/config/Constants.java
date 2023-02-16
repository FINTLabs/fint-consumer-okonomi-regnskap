package no.fint.consumer.config;

public enum Constants {
;

    public static final String COMPONENT = "okonomi-regnskap";
    public static final String COMPONENT_CONSUMER = COMPONENT + " consumer";
    public static final String CACHE_SERVICE = "CACHE_SERVICE";

    
    public static final String CACHE_INITIALDELAY_LEVERANDOR = "${fint.consumer.cache.initialDelay.leverandor:900000}";
    public static final String CACHE_FIXEDRATE_LEVERANDOR = "${fint.consumer.cache.fixedRate.leverandor:900000}";
    
    public static final String CACHE_INITIALDELAY_LEVERANDORGRUPPE = "${fint.consumer.cache.initialDelay.leverandorgruppe:1000000}";
    public static final String CACHE_FIXEDRATE_LEVERANDORGRUPPE = "${fint.consumer.cache.fixedRate.leverandorgruppe:900000}";
    
    public static final String CACHE_INITIALDELAY_POSTERING = "${fint.consumer.cache.initialDelay.postering:1100000}";
    public static final String CACHE_FIXEDRATE_POSTERING = "${fint.consumer.cache.fixedRate.postering:900000}";
    
    public static final String CACHE_INITIALDELAY_TRANSAKSJON = "${fint.consumer.cache.initialDelay.transaksjon:1200000}";
    public static final String CACHE_FIXEDRATE_TRANSAKSJON = "${fint.consumer.cache.fixedRate.transaksjon:900000}";
    

}
