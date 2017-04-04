package dubsapp.core.tool.rest;

public abstract class AbstractRequest extends AbsRequest {

    public AbstractRequest(){
        super();
    }

    public AbstractRequest(int methodType) {
        super(methodType);
    }
}