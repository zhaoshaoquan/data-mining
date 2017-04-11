package com.jdrx.dm.baseinfo.commons;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ZIP压缩文件操作工具类
 * 支持密码
 * 依赖zip4j开源项目
 * Created by 赵少泉 on 2016-07-12.
 */
public class ZipUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 解压指定的ZIP压缩文件到当前目录
     * @param zip 指定的ZIP压缩文件
     * @return 解压后文件数组
     */
    public static File[] unzip(String zip){
        return unzip(zip, null);
    }

    /**
     * 解压指定的ZIP压缩文件到指定目录
     * <p>
     * 如果指定目录不存在,可以自动创建,不合法的路径将返回null
     * @param zip 指定的ZIP压缩文件
     * @param dest 解压目录
     * @return 解压后文件数组
     */
    public static File[] unzip(String zip, String dest){
        return unzip(zip, dest, null);
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到指定目录
     * <p>
     * 如果指定目录不存在,可以自动创建,不合法的路径将返回null
     * @param zip 指定的ZIP压缩文件
     * @param dest 解压目录
     * @param passwd ZIP文件的密码
     * @return 解压后文件数组
     */
    public static File[] unzip(String zip, String dest, String passwd){
        File zipFile = new File(zip);
        String parentDir = StringUtils.isNotBlank(dest) ? dest : zipFile.getParentFile().getAbsolutePath();
        return unzip(zipFile, parentDir, passwd);
    }

    /**
     * 使用给定密码解压指定的ZIP压缩文件到指定目录
     * <p>
     * 如果指定目录不存在,可以自动创建,不合法的路径将返回null
     * @param zipFile 指定的ZIP压缩文件
     * @param dest 解压目录
     * @param passwd ZIP文件的密码
     * @return  解压后文件数组
     */
    public static File [] unzip(File zipFile, String dest, String passwd){
        try{
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");
            if(!zFile.isValidZipFile()){
                logger.error("压缩文件不合法,可能被损坏.");
                return null;
            }
            File destDir = new File(dest);
            if(destDir.isDirectory() && !destDir.exists()){
                destDir.mkdir();
            }
            if(zFile.isEncrypted()){
                zFile.setPassword(passwd.toCharArray());
            }
            zFile.extractAll(dest);
            List<FileHeader> headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            for(FileHeader fileHeader : headerList){
                if(!fileHeader.isDirectory()){
                    extractedFileList.add(new File(destDir,fileHeader.getFileName()));
                }
            }
            File [] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            return extractedFiles;
        }catch(ZipException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 压缩指定文件到当前文件夹
     * @param src 要压缩的指定文件
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src){
        return zip(src, null);
    }

    /**
     * 压缩指定文件到当前文件夹
     * @param src 要压缩的指定文件
     * @param isCreateDir 是否在压缩文件里创建目录,仅在压缩文件为目录时有效.<br/>
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src, boolean isCreateDir){
        return zip(src, null, isCreateDir);
    }

    /**
     * 压缩指定文件或文件夹到指定目录
     * @param src 要压缩的文件
     * @param dest 压缩文件存放路径
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src, String dest){
        return zip(src, dest, null);
    }

    /**
     * 压缩指定文件或文件夹到指定目录
     * @param src 要压缩的文件
     * @param dest 压缩文件存放路径
     * @param isCreateDir 是否在压缩文件里创建目录,仅在压缩文件为目录时有效.<br/>
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src, String dest, boolean isCreateDir){
        return zip(src, dest, isCreateDir, null);
    }

    /**
     * 使用给定密码压缩指定文件或文件夹到指定目录
     * @param src 要压缩的文件
     * @param dest 压缩文件存放路径
     * @param passwd 压缩使用的密码
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src, String dest, String passwd){
        return zip(src, dest, true, passwd);
    }

    /**
     * 使用给定密码压缩指定文件或文件夹到指定位置.
     * <p>
     * dest可传最终压缩文件存放的绝对路径,也可以传存放目录,也可以传null或者"".<br/>
     * 如果传null或者""则将压缩文件存放在当前目录,即跟源文件同目录,压缩文件名取源文件名,以.zip为后缀;<br/>
     * 如果以路径分隔符(File.separator)结尾,则视为目录,压缩文件名取源文件名,以.zip为后缀,否则视为文件名.
     * @param src 要压缩的文件或文件夹路径
     * @param dest 压缩文件存放路径
     * @param isCreateDir 是否在压缩文件里创建目录,仅在压缩文件为目录时有效.<br/>
     * 如果为false,将直接压缩目录下文件到压缩文件.
     * @param passwd 压缩使用的密码
     * @return 最终的压缩文件存放的绝对路径,如果为null则说明压缩失败.
     */
    public static String zip(String src, String dest, boolean isCreateDir, String passwd){
        File srcFile = new File(src);
        dest = buildDestinationZipFilePath(srcFile, dest);
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        if(!StringUtils.isEmpty(passwd)){
            parameters.setEncryptFiles(true);
            // 加密方式
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(passwd.toCharArray());
        }
        try{
            ZipFile zipFile = new ZipFile(dest);
            if(srcFile.isDirectory()){
                // 如果不创建目录的话,将直接把给定目录下的文件压缩到压缩文件,即没有目录结构
                if(!isCreateDir){
                    File [] subFiles = srcFile.listFiles();
                    ArrayList<File> temp = new ArrayList<>();
                    Collections.addAll(temp, subFiles);
                    zipFile.addFiles(temp, parameters);
                    return dest;
                }
                zipFile.addFolder(srcFile, parameters);
            }else{
                zipFile.addFile(srcFile, parameters);
            }
            return dest;
        }catch(ZipException e){
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构建压缩文件存放路径,如果不存在将会创建
     * 传入的可能是文件名或者目录,也可能不传,此方法用以转换最终压缩文件的存放路径
     * @param srcFile 源文件
     * @param destParam 压缩目标路径
     * @return 正确的压缩文件存放路径
     */
    private static String buildDestinationZipFilePath(File srcFile, String destParam){
        if(StringUtils.isEmpty(destParam)){
            if(srcFile.isDirectory()){
                destParam = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            }else{
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                destParam = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        }else{
            // 在指定路径不存在的情况下将其创建出来
            createDestDirectoryIfNecessary(destParam);
            if(destParam.endsWith(File.separator)){
                String fileName = srcFile.isDirectory() ? srcFile.getName() : srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                destParam += fileName + ".zip";
            }
        }
        return destParam;
    }

    /**
     * 在必要的情况下创建压缩文件存放目录,比如指定的存放路径并没有被创建
     * @param destParam 指定的存放路径,有可能该路径并没有被创建
     */
    private static void createDestDirectoryIfNecessary(String destParam){
        File destDir;
        if(destParam.endsWith(File.separator)){
            destDir = new File(destParam);
        }else{
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if(!destDir.exists()){
            destDir.mkdirs();
        }
    }

}
