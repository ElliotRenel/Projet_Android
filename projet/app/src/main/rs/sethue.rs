#pragma version (1)
#pragma rs java_package_name(com.example.editionimage))
//# include "hsv.rs"



//uchar4 RS_KERNEL sethue(uchar4 in ) {
//    float4 pixelRgb=rsUnpackColor8888(in);
//    float4 pixelHsv = rgbtohsv(in);
//    pixelHsv.x = 60;
//
//    uchar4 result = hsvtorgb(pixelHsv);
//    result.a=pixelRgb.a;
//    return result;
//}

uchar4 RS_KERNEL sethue(uchar4 in ) {
    float4 pixelf=rsUnpackColor8888(in);
    float gray=(0.30*pixelf.r+0.59* pixelf.g+0.11*pixelf.b);
    return rsPackColorTo8888(gray,gray,gray,pixelf.a);
}