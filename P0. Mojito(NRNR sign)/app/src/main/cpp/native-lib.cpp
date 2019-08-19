#include <jni.h>
#include <opencv2/opencv.hpp>

//using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_nrnrsign_MainActivity_thresholding(JNIEnv *env,
                                                    jobject instance,
                                                    jlong matAddrInput,
                                                    jlong matAddrResult) {

    cv::Mat &matInput = *(cv::Mat *)matAddrInput;
    cv::Mat &matResult = *(cv::Mat *)matAddrResult;

    cv::cvtColor(matInput, matInput, cv::COLOR_BGRA2GRAY);
    cv::threshold(matInput, matInput, 0, 255, cv::THRESH_BINARY|cv::THRESH_OTSU);

    cv::cvtColor(matInput, matResult, cv::COLOR_GRAY2BGRA, 4);

    for(size_t i = 0; i < matResult.rows; i++){
        for(size_t j = 0; j < matResult.cols; j++){
            int val = matInput.at<uchar>(i,j);
            //matResult.at<cv::Vec4b>(i,j)[0] = 0;
            //matResult.at<cv::Vec4b>(i,j)[1] = 0;
            matResult.at<cv::Vec4b>(i,j)[3] = 255 - val;

        }
    }
}