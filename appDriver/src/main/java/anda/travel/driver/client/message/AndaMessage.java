package anda.travel.driver.client.message;

/**
 * AndaMessage
 *
 * @author Zoro
 * @date 2017/3/27
 */
public class AndaMessage<B> {

    private Header header;
    private B body;

    public AndaMessage() {

    }

    public AndaMessage(Header header, B body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public B getBody() {
        return body;
    }

    public void setBody(B body) {
        this.body = body;
    }

}
