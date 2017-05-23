package indi.yume.demo.newapplication.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.annimon.stream.Optional;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;

import indi.yume.tools.autosharedpref.model.Func1;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

/**
 * Created by yume on 16-7-12.
 */
@UtilityClass
public class RxUtil {

    public static <T> Consumer<T> emptyAction() {
        return t -> {};
    }

    public static <T> SwitchTransform<T> switchThread(){
        return switchThread(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    public static <T> SwitchTransform<T> switchThread(Scheduler subscribeOn, Scheduler observeOn){
        return new SwitchTransform<>(subscribeOn, observeOn);
    }

    public static void postUI(Runnable runnable) {
        AndroidSchedulers.mainThread().createWorker().schedule(runnable);
    }

    public static void clearCache(Context context) {
        TempPath.clear(context);
    }

    public static Observable<File> getExTempFile(Activity activity, String fileName, String extension) {
        final String tmpFileName = getTempFileName(fileName, extension);
        return new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .flatMap(granted -> {
                    if(granted)
                        return Observable.just(TempPath.unit(activity).createFile(tmpFileName));
                    else
                        return Observable.empty();
                });
    }

    public static Observable<File> getExTempFile(Activity activity, String fileName) {
        return new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .flatMap(granted -> {
                    if(granted)
                        return Observable.just(
                                TempPath.unit(activity)
                                        .createFile(TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName));
                    else
                        return Observable.empty();
                });
    }

    public static Observable<File> getTempFile(Activity activity, String fileName, String extension) {
        final String tmpFileName = getTempFileName(fileName, extension);
        return new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .map(granted -> TempPath.unit(activity).createFile(tmpFileName));
    }

    public static Observable<File> getTempFile(Activity activity, String fileName) {
        return new RxPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .map(granted -> TempPath.unit(activity)
                            .createFile(TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName));
    }

    private static String getTempFileName(String fileName, String extension) {
        String file = (TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName);
        String ext ="." + (TextUtils.isEmpty(extension) ? "tmp" : extension);
        if(!file.endsWith(ext))
            file = file + ext;
        return file;
    }

    public static Func1<Response<ResponseBody>, Observable<File>> toFile(Activity activity, String fileName) {
        return toFile(getTempFile(activity, fileName));
    }

    public static Func1<Response<ResponseBody>, Observable<File>> toFile(final File targetFile) {
        return response -> toFile(response, Observable.just(targetFile));
    }

    public static Func1<Response<ResponseBody>, Observable<File>> toFile(Observable<File> targetFile) {
        return response -> toFile(response, targetFile);
    }

    public static Observable<File> toFile(Response<ResponseBody> response, Observable<File> targetFile) {
        return targetFile.flatMap(f -> toFile(response, f));
    }

    public static Observable<File> toFile(Response<ResponseBody> response, File targetFile) {
        if(!response.isSuccessful())
            return Observable.just(targetFile);

        BufferedSink sink = null;
        try {
            sink = Okio.buffer(Okio.sink(targetFile));
            // you can access body of response
            sink.writeAll(response.body().source());
            sink.close();
            return Observable.just(targetFile);
        } catch (IOException e) {
            targetFile.delete();
            return Observable.error(e);
        } finally {
            if (sink != null)
                try {
                    sink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @RequiredArgsConstructor
    static class SwitchTransform<T> implements ObservableTransformer<T, T>,
            FlowableTransformer<T, T>,
            MaybeTransformer<T, T>,
            SingleTransformer<T, T>,
            CompletableTransformer {

        private final Scheduler subscribeOn;
        private final Scheduler observeOn;

        @Override
        public CompletableSource apply(Completable upstream) {
            return upstream.subscribeOn(subscribeOn)
                    .observeOn(observeOn);
        }

        @Override
        public Publisher<T> apply(Flowable<T> upstream) {
            return upstream.subscribeOn(subscribeOn)
                    .observeOn(observeOn);
        }

        @Override
        public MaybeSource<T> apply(Maybe<T> upstream) {
            return upstream.subscribeOn(subscribeOn)
                    .observeOn(observeOn);
        }

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream.subscribeOn(subscribeOn)
                    .observeOn(observeOn);
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            return upstream.subscribeOn(subscribeOn)
                    .observeOn(observeOn);
        }
    }
}

abstract class TempPath {
    private static final String LOCAL_DIR_PATH = "temp";
    private static final String EXTERNAL_DIR_PATH = "Android/data/indi.yume.demo.newapplication/cache/temp";

    static TempPath unit(Context context) {
        return getExDir()
                .<TempPath>map(ExPath::new)
                .orElseGet(() -> getInDir(context)
                        .map(InPath::new)
                        .get());
    }

    static void clear(Context context) {
        getExDir().ifPresent(TempPath::delete);
        getInDir(context).ifPresent(TempPath::delete);
    }


    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    static Optional<File> getExDir() {
        File fileDir = null;
        if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            try {
                final File f = Environment.getExternalStorageDirectory();
                fileDir = new File(f, EXTERNAL_DIR_PATH);
                if(!fileDir.exists()) {
                    if(!fileDir.mkdirs())
                        fileDir = null;
                }
            } catch(Exception e) {
                fileDir = null;
            }
        }
        return Optional.ofNullable(fileDir);
    }

    static Optional<File> getInDir(Context context) {
        File fileDir = null;
        try {
            fileDir = context.getDir(LOCAL_DIR_PATH, Activity.MODE_WORLD_READABLE);
            if(!fileDir.exists())
            {
                if(!fileDir.mkdirs())
                    fileDir = null;
            }
        } catch (Exception e) {
            fileDir = null;
        }
        return Optional.ofNullable(fileDir);
    }

    @RequiredArgsConstructor
    static class ExPath extends TempPath{
        private final File exDir;

        @Override
        public File createFile(String fileName) {
            return new File(exDir, fileName);
        }
    }

    @RequiredArgsConstructor
    static class InPath extends TempPath{
        private final File inDir;

        @Override
        public File createFile(String fileName) {
            return new File(inDir, fileName);
        }
    }

    abstract File createFile(String fileName);
}