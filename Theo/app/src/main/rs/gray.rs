#pragma version(1)
#pragma rs java_package_name ( com.example.effectsapp)

uchar4 RS_KERNEL toGray ( uchar4 in ) {
    const uchar gray = (30* in.r
                       + 59* in.g
                       + 11* in.b ) /100;

    return ( uchar4 ) { gray, gray, gray, in.a };
}