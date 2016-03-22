package ai.subut.kurjun.model.repository;


/**
 * Transport protocol enumeration type.
 */
public enum Protocol {
    HTTP( false, true ), HTTPS ( true, true ), SSH( true, false );


    private final boolean secure;
    private final boolean webBased;


    private Protocol( boolean secure, boolean webBased ) {
        this.secure = secure;
        this.webBased = webBased;
    }


    public boolean isSecure() {
        return secure;
    }


    public boolean isWebBased() {
        return webBased;
    }
}
