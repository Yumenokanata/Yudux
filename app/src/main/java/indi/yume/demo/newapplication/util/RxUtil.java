//package indi.yume.demo.newapplication.util;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.os.Environment;
//import android.text.TextUtils;
//
//import com.annimon.stream.Optional;
//import com.tbruyelle.rxpermissions2.RxPermissions;
//
//import java.io.File;
//import java.io.IOException;
//
//import indi.yume.tools.avocado.functional.Composition;
//import indi.yume.tools.avocado.functional.Memoization;
//import io.reactivex.Flowable;
//import io.reactivex.FlowableTransformer;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import io.reactivex.functions.Function;
//import io.reactivex.schedulers.Schedulers;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.UtilityClass;
//import lombok.val;
//import okhttp3.ResponseBody;
//import okio.BufferedSink;
//import okio.Okio;
//import retrofit2.Response;
//
///**
// * Created by yume on 16-7-12.
// */
//@UtilityClass
//public class RxUtil {
//
//    public static <T> Consumer<T> emptyAction() {
//        return t -> {};
//    }
//
//    public static <T> FlowableTransformer<T, T> switchThread(){
//        return flo -> flo.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static <T, R> FlowableTransformer<T, R> memoize(Function<T, Flowable<R>> f) {
//        val fM = Memoization.memoize(Composition.andThen(f, RxUtil::getSingleData));
//        return obs -> obs.flatMap(m -> Flowable.from(fM.call(m)));
//    }
//
//    private static <T> Iterable<T> getSingleData(Observable<T> obs) {
//        return obs.toBlocking().toIterable();
//    }
//
//    public static void clearCache(Context context) {
//        TempPath.clear(context);
//    }
//
//    public static Observable<File> getExTempFile(Activity activity, String fileName, String extension) {
//        final String tmpFileName = getTempFileName(fileName, extension);
//        return new RxPermissions(activity)
//                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .flatMap(granted -> {
//                    if(granted)
//                        return Observable.just(TempPath.unit(activity).createFile(tmpFileName));
//                    else
//                        return Observable.empty();
//                });
//    }
//
//    public static Observable<File> getExTempFile(Activity activity, String fileName) {
//        return new RxPermissions(activity)
//                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .flatMap(granted -> {
//                    if(granted)
//                        return Observable.just(
//                                TempPath.unit(activity)
//                                        .createFile(TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName));
//                    else
//                        return Observable.empty();
//                });
//    }
//
//    public static Observable<File> getTempFile(Activity activity, String fileName, String extension) {
//        final String tmpFileName = getTempFileName(fileName, extension);
//        return new RxPermissions(activity)
//                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .map(granted -> TempPath.unit(activity).createFile(tmpFileName));
//    }
//
//    public static Observable<File> getTempFile(Activity activity, String fileName) {
//        return new RxPermissions(activity)
//                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .map(granted -> TempPath.unit(activity)
//                            .createFile(TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName));
//    }
//
//    private static String getTempFileName(String fileName, String extension) {
//        String file = (TextUtils.isEmpty(fileName) ? String.valueOf(System.currentTimeMillis()) : fileName);
//        String ext ="." + (TextUtils.isEmpty(extension) ? "tmp" : extension);
//        if(!file.endsWith(ext))
//            file = file + ext;
//        return file;
//    }
//
//    public static Func1<Response<ResponseBody>, Observable<File>> toFile(Activity activity, String fileName) {
//        return toFile(getTempFile(activity, fileName));
//    }
//
//    public static Func1<Response<ResponseBody>, Observable<File>> toFile(final File targetFile) {
//        return response -> toFile(response, Observable.just(targetFile));
//    }
//
//    public static Func1<Response<ResponseBody>, Observable<File>> toFile(Observable<File> targetFile) {
//        return response -> toFile(response, targetFile);
//    }
//
//    public static Observable<File> toFile(Response<ResponseBody> response, Observable<File> targetFile) {
//        return targetFile.flatMap(f -> toFile(response, f));
//    }
//
//    public static Observable<File> toFile(Response<ResponseBody> response, File targetFile) {
//        if(!response.isSuccessful())
//            return Observable.just(targetFile);
//
//        BufferedSink sink = null;
//        try {
//            sink = Okio.buffer(Okio.sink(targetFile));
//            // you can access body of response
//            sink.writeAll(response.body().source());
//            sink.close();
//            return Observable.just(targetFile);
//        } catch (IOException e) {
//            targetFile.delete();
//            return Observable.error(e);
//        } finally {
//            if (sink != null)
//                try {
//                    sink.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//    }
//}
//
//abstract class TempPath {
//    private static final String LOCAL_DIR_PATH = "temp";
//    private static final String EXTERNAL_DIR_PATH = "Android/data/indi.yume.demo.newapplication/cache/temp";
//
//    static TempPath unit(Context context) {
//        return getExDir()
//                .<TempPath>map(ExPath::new)
//                .orElseGet(() -> getInDir(context)
//                        .map(InPath::new)
//                        .get());
//    }
//
//    static void clear(Context context) {
//        getExDir().ifPresent(TempPath::delete);
//        getInDir(context).ifPresent(TempPath::delete);
//    }
//
//
//    public static void delete(File file) {
//        if (file.isFile()) {
//            file.delete();
//            return;
//        }
//
//        if(file.isDirectory()){
//            File[] childFiles = file.listFiles();
//            if (childFiles == null || childFiles.length == 0) {
//                file.delete();
//                return;
//            }
//
//            for (int i = 0; i < childFiles.length; i++) {
//                delete(childFiles[i]);
//            }
//            file.delete();
//        }
//    }
//
//    static Optional<File> getExDir() {
//        File fileDir = null;
//        if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
//            try {
//                final File f = Environment.getExternalStorageDirectory();
//                fileDir = new File(f, EXTERNAL_DIR_PATH);
//                if(!fileDir.exists()) {
//                    if(!fileDir.mkdirs())
//                        fileDir = null;
//                }
//            } catch(Exception e) {
//                fileDir = null;
//            }
//        }
//        return Optional.ofNullable(fileDir);
//    }
//
//    static Optional<File> getInDir(Context context) {
//        File fileDir = null;
//        try {
//            fileDir = context.getDir(LOCAL_DIR_PATH, Activity.MODE_WORLD_READABLE);
//            if(!fileDir.exists())
//            {
//                if(!fileDir.mkdirs())
//                    fileDir = null;
//            }
//        } catch (Exception e) {
//            fileDir = null;
//        }
//        return Optional.ofNullable(fileDir);
//    }
//
//    @RequiredArgsConstructor
//    static class ExPath extends TempPath{
//        private final File exDir;
//
//        @Override
//        public File createFile(String fileName) {
//            return new File(exDir, fileName);
//        }
//    }
//
//    @RequiredArgsConstructor
//    static class InPath extends TempPath{
//        private final File inDir;
//
//        @Override
//        public File createFile(String fileName) {
//            return new File(inDir, fileName);
//        }
//    }
//
//    abstract File createFile(String fileName);
//}