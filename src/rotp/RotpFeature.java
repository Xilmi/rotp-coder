package rotp;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.hosted.FeatureImpl;
import com.oracle.svm.hosted.c.NativeLibraries;
import org.graalvm.nativeimage.hosted.Feature;

@AutomaticFeature
public class RotpFeature implements Feature {

    public static void hello() {
        System.out.println("TTTT INIT");
    }

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        // needed on Linux for Robot classes
        NativeLibraries nativeLibraries = ((FeatureImpl.BeforeAnalysisAccessImpl) access).getNativeLibraries();
        nativeLibraries.addDynamicNonJniLibrary("Xtst");
        System.out.println("TTTT HELLO");
    }
}
