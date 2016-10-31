package com.easemob.helpdeskdemo.filedownload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by liyuzhao on 16/7/21.
 */
public class FileResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public FileResponseBody(ResponseBody responseBody){
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if(bufferedSource == null)
        {
            try{
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }catch (Exception e){e.printStackTrace();}
        }
        return bufferedSource;

    }


    private Source source(Source source)
    {
        return new ForwardingSource(source)
        {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException
            {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                RxBus.getInstance().post(new FileLoadEvent(contentLength(), totalBytesRead));
                // progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }



}
