package com.hotdog.hdlibrary.http;

import com.hotdog.hdlibrary.utils.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * http同步请求结果转换器接口,自带3个结果转换器,有需要请自行扩展
 */

public interface IParser<Result> {

    Result parse(HDHttpResponse body) throws IOException;

    class BytesParser implements IParser<byte[]> {

        @Override
        public byte[] parse(HDHttpResponse body) throws IOException {
            return IOUtils.stream2Bytes(body.contentStream());
        }

    }

    /**
     * String 转换器
     */
    class StringParser implements IParser<String> {

        @Override
        public String parse(HDHttpResponse body) throws IOException {
            return IOUtils.stream2String(body.contentStream());
        }

    }

    /**
     * JSONObject 转换器
     */
    class JSONObjectParser implements IParser<JSONObject> {

        @Override
        public JSONObject parse(HDHttpResponse body) throws IOException {
            String s = IOUtils.stream2String(body.contentStream());
            try {
                return new JSONObject(s);
            } catch (JSONException e) {
                throw new IOException(String.format("%s is not a JSONObject !", s));
            }
        }

    }

    /**
     * JSONArray 转换器
     */
    class JSONArrayParser implements IParser<JSONArray> {

        @Override
        public JSONArray parse(HDHttpResponse body) throws IOException {
            String s = IOUtils.stream2String(body.contentStream());
            try {
                return new JSONArray(s);
            } catch (JSONException e) {
                throw new IOException(String.format("%s is not a JSONArray !", s));
            }
        }

    }

    class FileParser implements IParser<File> {

    	private File outFile;

    	public FileParser(File outFile) {
			this.outFile = outFile;
	    }

	    @Override
	    public File parse(HDHttpResponse body) throws IOException {
		    FileOutputStream fos = new FileOutputStream(outFile);
		    IOUtils.copy(body.contentStream(), fos, true);
		    return outFile;
	    }

    }

}
