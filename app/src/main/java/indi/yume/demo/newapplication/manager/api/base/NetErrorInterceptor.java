package indi.yume.demo.newapplication.manager.api.base;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

import indi.yume.demo.newapplication.model.api.CommonModel;
import indi.yume.demo.newapplication.util.LogUtil;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by yume on 16/11/15.
 *
 * Used for check service error.
 *
 * Example:
 * {"common":{"loginFlg":"1","msgType":"E","msgCode":"cmn.passwd.unable","message":"ユーザーＩＤまたはパスワードがまちがっています。<a class=\"cmn\" href=\"/\">ホームページへ戻りログインしなおしてください。<\/a>"}}
 *
 * @author xuemao.tang
 *
 */
public class NetErrorInterceptor implements Interceptor {
    private Gson gson;

    public NetErrorInterceptor(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if(chain == null)
            return null;

        Response response = chain.proceed(chain.request());

        ResponseBody responseBody = response.body();

        MediaType contentType = responseBody.contentType();
        if(contentType != null && !contentType.toString().contains("json"))
            return response;

        Long contentLength = responseBody.contentLength();

        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE);
        } catch (IOException e) {
            return response;
        }

        Buffer buffer = source.buffer();

        Charset charset = Charset.forName("UTF-8");
        if (contentType != null) {
            charset = contentType.charset(charset);
        }

        if (contentLength != 0L) {
            CommonModel errorModel = null;
            try {
                String data = buffer.clone().readString(charset);

                errorModel = gson.fromJson(data, CommonModel.class);
            } catch(Exception e){
                LogUtil.e(e);
            }
            if(errorModel != null && !TextUtils.isEmpty(errorModel.getCode())) {
                errorModel.setUrl(chain.request().url().toString());
                throw new NetErrorException(errorModel);
            }
        }

        return response;
    }
}
