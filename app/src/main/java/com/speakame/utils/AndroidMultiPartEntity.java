package com.speakame.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;


/**
 * Created by MAX on 20-Mar-17.
 */
@SuppressWarnings("deprecation")
public class AndroidMultiPartEntity extends MultipartEntity {

    private final ProgressListener listener;
    private static boolean isUserInterrupt = false;
    private CountingOutputStream cos;

    public AndroidMultiPartEntity(final ProgressListener listener) {
        super();
        this.listener = listener;
        isUserInterrupt = false;
    }

    public AndroidMultiPartEntity(final HttpMultipartMode mode,
                                  final ProgressListener listener) {
        super(mode);
        this.listener = listener;
        isUserInterrupt = false;
    }

    public AndroidMultiPartEntity(HttpMultipartMode mode, final String boundary,
                                  final Charset charset, final ProgressListener listener) {
        super(mode, boundary, charset);
        this.listener = listener;
        isUserInterrupt = false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        //super.writeTo(new CountingOutputStream(outstream, this.listener));
        if(isUserInterrupt){
            if(cos!=null){
                try{
                    cos.flush();
                    cos.close();
                    cos = null;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        cos = new CountingOutputStream(outstream, this.listener);
        super.writeTo(cos);
    }

    public static interface ProgressListener {
        void transferred(long num);
    }
    public static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;
        private long transferred;

        public CountingOutputStream(final OutputStream out,
                                    final ProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            if(isUserInterrupt)
            {

                return;
            }
            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred);

        }

        public void write(int b) throws IOException {
            if(isUserInterrupt)
            {

                return;
            }
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);

        }
    }
    /*public static class CountingOutputStream extends FilterOutputStream {

        private final ProgressListener listener;
        private long transferred;

        public CountingOutputStream(final OutputStream out,
                                    final ProgressListener listener) {
            super(out);
            this.listener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            this.transferred += len;
            this.listener.transferred(this.transferred);
        }

        public void write(int b) throws IOException {
            out.write(b);
            this.transferred++;
            this.listener.transferred(this.transferred);
        }
    }*/
    public void cancelUploading(){
        isUserInterrupt = true;
        if(cos!=null){
            try{
                cos.flush();
                cos.close();
                cos = null;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}