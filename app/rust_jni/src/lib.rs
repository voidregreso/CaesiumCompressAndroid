#![allow(non_snake_case)]

use jni::objects::{JClass, JObject, JString};
use jni::sys::{jboolean, jstring, JNI_TRUE, JNI_FALSE};
use jni::JNIEnv;

use std::error::Error;
use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use jni::signature::{Primitive, ReturnType};

use crate::utils::get_filetype;

mod gif;
mod jpeg;
mod png;
mod resize;
mod utils;
mod webp;

#[repr(C)]
pub struct CCSParameters {
    pub keep_metadata: bool,
    pub jpeg_quality: u32,
    pub png_quality: u32,
    pub png_force_zopfli: bool,
    pub gif_quality: u32,
    pub webp_quality: u32,
    pub optimize: bool,
    pub width: u32,
    pub height: u32,
}

#[repr(C)]
pub struct CCSResult {
    pub success: bool,
    pub error_message: *const c_char,
}

pub struct CSParameters {
    pub jpeg: jpeg::Parameters,
    pub png: png::Parameters,
    pub gif: gif::Parameters,
    pub webp: webp::Parameters,
    pub keep_metadata: bool,
    pub optimize: bool,
    pub width: u32,
    pub height: u32,
}

pub fn initialize_parameters() -> CSParameters {
    let jpeg = jpeg::Parameters { quality: 80 };

    let png = png::Parameters {
        oxipng: oxipng::Options::default(),
        quality: 80,
        force_zopfli: false,
    };

    let gif = gif::Parameters { quality: 80 };

    let webp = webp::Parameters { quality: 80 };

    CSParameters {
        jpeg,
        png,
        gif,
        webp,
        keep_metadata: false,
        optimize: false,
        width: 0,
        height: 0,
    }
}

pub fn my_compress(
    input_path: String,
    output_path: String,
    params: CCSParameters,
) -> CCSResult {
    let mut parameters = initialize_parameters();

    parameters.jpeg.quality = params.jpeg_quality;
    parameters.png.quality = params.png_quality;
    parameters.optimize = params.optimize;
    parameters.keep_metadata = params.keep_metadata;
    parameters.png.force_zopfli = params.png_force_zopfli;
    parameters.gif.quality = params.gif_quality;
    parameters.webp.quality = params.webp_quality;
    parameters.width = params.width;
    parameters.height = params.height;

    let mut error_message = CString::new("").unwrap();

    match compress(
        input_path,
        output_path,
        parameters,
    ) {
        Ok(_) => {
            let em_pointer = error_message.as_ptr();
            std::mem::forget(error_message);
            CCSResult {
                success: true,
                error_message: em_pointer,
            }
        }
        Err(e) => {
            error_message = CString::new(e.to_string()).unwrap();
            let em_pointer = error_message.as_ptr();
            std::mem::forget(error_message);
            CCSResult {
                success: false,
                error_message: em_pointer,
            }
        }
    }
}

pub fn compress(
    input_path: String,
    output_path: String,
    parameters: CSParameters,
) -> Result<(), Box<dyn Error>> {
    validate_parameters(&parameters)?;
    let file_type = get_filetype(&input_path);

    match file_type {
        utils::SupportedFileTypes::Jpeg => {
            jpeg::compress(input_path, output_path, parameters)?;
        }
        utils::SupportedFileTypes::Png => {
            png::compress(input_path, output_path, parameters)?;
        }
        utils::SupportedFileTypes::Gif => {
            gif::compress(input_path, output_path, parameters)?;
        }
        utils::SupportedFileTypes::WebP => {
            webp::compress(input_path, output_path, parameters)?;
        }
        _ => return Err("Unknown file type".into()),
    }

    Ok(())
}

fn validate_parameters(parameters: &CSParameters) -> Result<(), Box<dyn Error>> {
    if parameters.jpeg.quality == 0 || parameters.jpeg.quality > 100 {
        return Err("Invalid JPEG quality value".into());
    }

    if parameters.png.quality > 100 {
        return Err("Invalid PNG quality value".into());
    }

    if parameters.gif.quality > 100 {
        return Err("Invalid GIF quality value".into());
    }

    if parameters.webp.quality > 100 {
        return Err("Invalid WebP quality value".into());
    }

    Ok(())
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_ernesto_libcaesium_CaesiumNative_compressPic (
    env: JNIEnv,
    _clz: JClass,
    inf: JString,
    outf: JString,
    conf: JObject
) -> jboolean {
    let entrada: String = env.get_string(inf).unwrap().into();
    let salida: String = env.get_string(outf).unwrap().into();

    // Get class object of CCSParameter
    let cl = env.find_class("com/ernesto/libcaesium/CCSParameter").unwrap();
    // Get field ID from class CCSParameter
    let f_keep_metadata = env.get_field_id(cl, "keep_metadata", "Z").unwrap();
    let f_jpeg_quality = env.get_field_id(cl, "jpeg_qu", "I").unwrap();
    let f_png_quality = env.get_field_id(cl, "png_qu", "I").unwrap();
    let f_png_force_zopfli = env.get_field_id(cl, "png_force_zopfli", "Z").unwrap();
    let f_gif_quality = env.get_field_id(cl, "gif_qu", "I").unwrap();
    let f_webp_quality = env.get_field_id(cl, "webp_qu", "I").unwrap();
    let f_optimize = env.get_field_id(cl, "optm", "Z").unwrap();
    let f_width = env.get_field_id(cl, "width", "I").unwrap();
    let f_height = env.get_field_id(cl, "height", "I").unwrap();

    // Read field value from by ID of class CCSParameter
    let km: bool = env.get_field_unchecked(
        conf,
        f_keep_metadata,
        ReturnType::Primitive(Primitive::Boolean),
    ).unwrap().z().unwrap();
    let jq: u32 = env.get_field_unchecked(
        conf,
        f_jpeg_quality,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;
    let pq: u32 = env.get_field_unchecked(
        conf,
        f_png_quality,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;
    let pfz: bool = env.get_field_unchecked(
        conf,
        f_png_force_zopfli,
        ReturnType::Primitive(Primitive::Boolean),
    ).unwrap().z().unwrap();
    let gq: u32 = env.get_field_unchecked(
        conf,
        f_gif_quality,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;
    let wq: u32 = env.get_field_unchecked(
        conf,
        f_webp_quality,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;
    let opt: bool = env.get_field_unchecked(
        conf,
        f_optimize,
        ReturnType::Primitive(Primitive::Boolean),
    ).unwrap().z().unwrap();
    let ww: u32 = env.get_field_unchecked(
        conf,
        f_width,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;
    let hh: u32 = env.get_field_unchecked(
        conf,
        f_height,
        ReturnType::Primitive(Primitive::Int),
    ).unwrap().i().unwrap() as u32;

    // Fill parameters
    let params = CCSParameters {
        keep_metadata: km,
        jpeg_quality: jq,
        png_quality: pq,
        png_force_zopfli: pfz,
        gif_quality: gq,
        webp_quality: wq,
        optimize: opt,
        width: ww,
        height: hh
    };

    let res: CCSResult = my_compress(entrada, salida, params);
    //let val: JString = env.new_string(CStr::from_ptr(res.error_message).to_str().unwrap()).expect("Couldn't create Java string!");
    let rtn: jboolean = if res.success {JNI_TRUE} else {JNI_FALSE};
    rtn.into()
}
