#pragma version (1)
#pragma rs java_package_name (com.example.td1)

uchar4 RS_KERNEL keephue (uchar4 in ) {
    return (in);
}

//# include "hsv.rs"

//uchar4 RS_KERNEL keephue(uchar4 in ) {
//    float4 pixelRgb=rsUnpackColor8888(in);
    //float alpha = pixelRgb.a;
    //uchar4 result = rsPackColorTo8888(pixelRgb);//hsvtorgb(rgbtohsv(pixelRgb));
    //result.a=alpha;
//    float red = pixelRgb.r;
//    float green = pixelRgb.g;
//    float blue = pixelRgb.b;
//    return rsPackColorTo8888(red,green,blue,pixelRgb.a);//result;
//}