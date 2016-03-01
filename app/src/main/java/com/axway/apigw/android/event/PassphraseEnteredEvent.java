package com.axway.apigw.android.event;

/**
 * Created by su on 5/8/2015.
 */
public class PassphraseEnteredEvent {

    private String domainPhrase;
    private String tempPhrase;
    private int sslOption;

    public PassphraseEnteredEvent(int sslOpt, String domainPhrase, String tempPhrase) {
        super();
        this.sslOption = sslOpt;
        this.domainPhrase = domainPhrase;
        this.tempPhrase = tempPhrase;
    }

//    public PassphraseEnteredEvent(String domainPhrase, String tempPhrase, String signAlg, String p12Path) {
//        this(domainPhrase, tempPhrase, signAlg);
//        this.p12Path = p12Path;
//    }

    public String getDomainPhrase() {
        return domainPhrase;
    }

    public String getTempPhrase() {
        return tempPhrase;
    }

    public int getSslOption() {
        return sslOption;
    }

    public void setSslOption(int sslOption) {
        this.sslOption = sslOption;
    }
}
