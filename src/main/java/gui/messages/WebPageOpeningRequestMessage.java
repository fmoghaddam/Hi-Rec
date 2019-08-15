package gui.messages;

public class WebPageOpeningRequestMessage {
    private final String webpageURL;

    public WebPageOpeningRequestMessage(String webpageURL) {
        this.webpageURL = webpageURL;
    }

    public String getWebpageURL() {
        return webpageURL;
    }
}
