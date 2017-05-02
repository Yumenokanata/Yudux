package indi.yume.demo.newapplication.manager.api.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest.AuthenticationRequestBuilder;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import indi.yume.demo.newapplication.BuildConfig;
import indi.yume.demo.newapplication.manager.api.auth.ApiKeyAuth;
import indi.yume.demo.newapplication.manager.api.auth.HttpBasicAuth;
import indi.yume.demo.newapplication.manager.api.auth.OAuth;
import indi.yume.demo.newapplication.manager.api.auth.OAuth.AccessTokenListener;
import indi.yume.demo.newapplication.util.LogUtil;
import io.reactivex.functions.Consumer;
import kotlin.Pair;
import lombok.Data;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Data
public class ApiClient {

    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;
    private final Gson gson;

    private ApiClient(Retrofit retrofit, OkHttpClient okHttpClient, Gson gson) {
        this.retrofit = retrofit;
        this.okHttpClient = okHttpClient;
        this.gson = gson;
    }

    public static ApiClient getDefaultClient() {
        return new Builder().build();
    }

    public <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static class Builder {
        private Map<String, Interceptor> apiAuthorizations;
        private OkHttpClient.Builder okBuilder;
        private Retrofit.Builder adapterBuilder;
        @Getter
        private Gson gson;

        public Builder() {
            apiAuthorizations = new LinkedHashMap<String, Interceptor>();
            settingDefault();
        }

        public Builder(String[] authNames) {
            this();
            for(String authName : authNames) {
                throw new RuntimeException("auth name \"" + authName + "\" not found in available auth names");
            }
        }

        /**
         * Basic constructor for single auth name
         * @param authName Authentication name
         */
        public Builder(String authName) {
            this(new String[]{authName});
        }

        /**
         * Helper constructor for single api key
         * @param authName Authentication name
         * @param apiKey API key
         */
        public Builder(String authName, String apiKey) {
            this(authName);
            this.setApiKey(apiKey);
        }

        /**
         * Helper constructor for single basic auth or password oauth2
         * @param authName Authentication name
         * @param username Username
         * @param password Password
         */
        public Builder(String authName, String username, String password) {
            this(authName);
            this.setCredentials(username,  password);
        }

        /**
         * Helper constructor for single password oauth2
         * @param authName Authentication name
         * @param clientId Client ID
         * @param secret Client Secret
         * @param username Username
         * @param password Password
         */
        public Builder(String authName, String clientId, String secret, String username, String password) {
            this(authName);
            this.getTokenEndPoint()
                    .setClientId(clientId)
                    .setClientSecret(secret)
                    .setUsername(username)
                    .setPassword(password);
        }

        private void settingDefault() {
            okBuilder = new OkHttpClient.Builder();
            adapterBuilder = new Retrofit.Builder();
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .registerTypeAdapter(int.class, new IntegerTypeAdapter())
                    .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
                    .create();
        }

        public Builder openFullLog() {
            okBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

            return this;
        }

        /**
         * Helper method to configure the first api key found
         * @param apiKey API key
         */
        private Builder setApiKey(String apiKey) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof ApiKeyAuth) {
                    ApiKeyAuth keyAuth = (ApiKeyAuth) apiAuthorization;
                    keyAuth.setApiKey(apiKey);
                    break;
                }
            }

            return this;
        }

        /**
         * Helper method to configure the username/password for basic auth or password oauth
         * @param username Username
         * @param password Password
         */
        private Builder setCredentials(String username, String password) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof HttpBasicAuth) {
                    HttpBasicAuth basicAuth = (HttpBasicAuth) apiAuthorization;
                    basicAuth.setCredentials(username, password);
                    break;
                }
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    oauth.getTokenRequestBuilder().setUsername(username).setPassword(password);
                    break;
                }
            }
            return this;
        }

        /**
         * Helper method to configure the token endpoint of the first oauth found in the apiAuthorizations (there should be only one)
         * @return Token request builder
         */
        public TokenRequestBuilder getTokenEndPoint() {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    return oauth.getTokenRequestBuilder();
                }
            }
            return null;
        }

        /**
         * Helper method to configure authorization endpoint of the first oauth found in the apiAuthorizations (there should be only one)
         * @return Authentication request builder
         */
        public AuthenticationRequestBuilder getAuthorizationEndPoint() {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    return oauth.getAuthenticationRequestBuilder();
                }
            }
            return null;
        }

        /**
         * Helper method to pre-set the oauth access token of the first oauth found in the apiAuthorizations (there should be only one)
         * @param accessToken Access token
         */
        public Builder setAccessToken(String accessToken) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    oauth.setAccessToken(accessToken);
                    break;
                }
            }

            return this;
        }

        /**
         * Helper method to configure the oauth accessCode/implicit flow parameters
         * @param clientId Client ID
         * @param clientSecret Client secret
         * @param redirectURI Redirect URI
         */
        public Builder configureAuthorizationFlow(String clientId, String clientSecret, String redirectURI) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    oauth.getTokenRequestBuilder()
                            .setClientId(clientId)
                            .setClientSecret(clientSecret)
                            .setRedirectURI(redirectURI);
                    oauth.getAuthenticationRequestBuilder()
                            .setClientId(clientId)
                            .setRedirectURI(redirectURI);
                    return this;
                }
            }

            return this;
        }

        /**
         * Configures a listener which is notified when a new access token is received.
         * @param accessTokenListener Access token listener
         */
        public Builder registerAccessTokenListener(AccessTokenListener accessTokenListener) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                if (apiAuthorization instanceof OAuth) {
                    OAuth oauth = (OAuth) apiAuthorization;
                    oauth.registerAccessTokenListener(accessTokenListener);
                    return this;
                }
            }

            return this;
        }

        /**
         * Adds an authorization to be used by the client
         * @param authName Authentication name
         * @param authorization Authorization interceptor
         */
        public Builder addAuthorization(String authName, Interceptor authorization) {
            if (apiAuthorizations.containsKey(authName)) {
                throw new RuntimeException("auth name \"" + authName + "\" already in api authorizations");
            }
            apiAuthorizations.put(authName, authorization);
            okBuilder.addInterceptor(authorization);

            return this;
        }

        public Map<String, Interceptor> getApiAuthorizations() {
            return apiAuthorizations;
        }

        public Builder setApiAuthorizations(Map<String, Interceptor> apiAuthorizations) {
            this.apiAuthorizations = apiAuthorizations;
            return this;
        }

        public Builder setAllowAllCerTificates(){
            Pair<SSLSocketFactory, X509TrustManager> data = getAllowAllSSLSocketFactory();
            okBuilder.sslSocketFactory(data.getFirst(), data.getSecond());
            okBuilder.hostnameVerifier((hostname, session) -> true);

            return this;
        }

        @NotNull
        private static Pair<SSLSocketFactory, X509TrustManager> getAllowAllSSLSocketFactory(){
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                X509TrustManager trustManager = getTrustManagerAllowAllCerts();
                sslContext.init(null,
                        new TrustManager[]{ trustManager },
                        new SecureRandom());

                return new Pair<>(sslContext.getSocketFactory(), trustManager);
            } catch (Exception e) {
                LogUtil.e(e);
                return new Pair<>(null, null);
            }
        }

        /**
         * すべての証明書を受け付ける信頼マネージャを作成
         * @return 信頼マネージャ
         */
        private static X509TrustManager getTrustManagerAllowAllCerts() {
            // すべての証明書を受け付ける信頼マネージャ
            return new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
					/*
					 * 認証するピアについて信頼されている、証明書発行局の証明書の配列を返します。
					 */
                    return new X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(
                        X509Certificate[] chain, String authType)
                        throws CertificateException {
					/*
					 * ピアから提出された一部のまたは完全な証明書チェーンを使用して、
					 * 信頼できるルートへの証明書パスを構築し、認証タイプに基づいて
					 * クライアント認証を検証できるかどうか、信頼できるかどうかを返します。
					 */
                }

                @Override
                public void checkServerTrusted(
                        X509Certificate[] chain, String authType)
                        throws CertificateException {
					/*
					 * ピアから提出された一部のまたは完全な証明書チェーンを使用して、
					 * 信頼できるルートへの証明書パスを構築し、認証タイプに基づいて
					 * サーバ認証を検証できるかどうか、また信頼できるかどうかを返します。
					 */
                }
            };
        }

        public Retrofit.Builder getAdapterBuilder() {
            return adapterBuilder;
        }

        public Builder setAdapterBuilder(Retrofit.Builder adapterBuilder) {
            this.adapterBuilder = adapterBuilder;

            return this;
        }

        public OkHttpClient.Builder getOkBuilder() {
            return okBuilder;
        }

        public Builder doForOkBuilder(Consumer<OkHttpClient.Builder> f) {
            try {
                f.accept(okBuilder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addAuthsToOkBuilder(OkHttpClient.Builder okBuilder) {
            for(Interceptor apiAuthorization : apiAuthorizations.values()) {
                okBuilder.addInterceptor(apiAuthorization);
            }

            return this;
        }

        public Builder setGson(Gson gson) {
            this.gson = gson;
            return this;
        }

        /**
         * Clones the okBuilder given in parameter, adds the auth interceptors and uses it to configure the Retrofit
         * @param okClient An instance of OK HTTP client
         */
        public Builder setOkClient(OkHttpClient okClient) {
            this.okBuilder = okClient.newBuilder();
            addAuthsToOkBuilder(this.okBuilder);

            return this;
        }

        public ApiClient build() {
            okBuilder.cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER)));

            String baseUrl = BuildConfig.BASE_URL;
            if(!baseUrl.endsWith("/"))
                baseUrl = baseUrl + "/";

            adapterBuilder
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonCustomConverterFactory.create(gson));

//            if (BuildConfig.ALLOW_ALL_CERTIFICATES)
//                setAllowAllCerTificates();

            OkHttpClient client = okBuilder.build();
            Retrofit retrofit = adapterBuilder.client(client).build();
            return new ApiClient(retrofit, client, gson);
        }
    }
}

/**
 * This wrapper is to take care of this case:
 * when the deserialization fails due to JsonParseException and the
 * expected type is String, then just return the body string.
 */
class GsonResponseBodyConverterToString<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverterToString(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        String returned = value.string();
        try {
            return gson.fromJson(returned, type);
        }
        catch (JsonParseException e) {
            return (T) returned;
        }
    }
}

class GsonCustomConverterFactory extends Converter.Factory
{
    public static GsonCustomConverterFactory create(Gson gson) {
        return new GsonCustomConverterFactory(gson);
    }

    private final Gson gson;
    private final GsonConverterFactory gsonConverterFactory;

    private GsonCustomConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.gsonConverterFactory = GsonConverterFactory.create(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if(type.equals(String.class))
            return new GsonResponseBodyConverterToString<Object>(gson, type);
        else
            return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
