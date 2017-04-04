package dubsapp.core.tool.rest;

public interface IRestParam {
        void addParam(String name, String value);

        void addMultiPart(String paramName, String filePath);
    }
