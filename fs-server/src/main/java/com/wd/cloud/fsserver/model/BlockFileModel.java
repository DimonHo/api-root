package com.wd.cloud.fsserver.model;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.StringJoiner;

/**
 * @author He Zhigang
 * @date 2018/11/10
 * @Description: 分片文件模型
 */
public class BlockFileModel {

    /**
     * 文件ID
     */
    private String id;
    /**
     * 文件目录
     */
    private String dir;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件总大小
     */
    private long fileSize;
    /**
     * 文件摘要
     */
    private String fileMd5;

    /**
     * 总分片数量
     */
    private int chunks;
    /**
     * 当前为第几块分片
     */
    private int chunkIndex;
    /**
     * 当前分片大小
     */
    private long blockSize;
    /**
     * 分片对象
     */
    private MultipartFile blockFile;
    /**
     * 分片摘要
     */
    private String blockMd5;

    public static BlockFileModel build(String dir, String fileMd5, MultipartFile blockFile, HttpServletRequest request) {
        BlockFileModel blockFileModel = new BlockFileModel();
        blockFileModel.setDir(dir)
                .setFileMd5(fileMd5)
                .setBlockFile(blockFile)
                .setBlockSize(blockFile.getSize());
        request.getParameterMap().forEach((k, v) -> {
            switch (k) {
                case "name":
                    blockFileModel.setFileName(v[0]);
                    break;
                case "size":
                    blockFileModel.setFileSize(Integer.valueOf(v[0]));
                    break;
                case "chunks":
                    blockFileModel.setChunks(Integer.valueOf(v[0]));
                    break;
                case "chunk":
                    blockFileModel.setChunkIndex(Integer.valueOf(v[0]));
                    break;
                case "id":
                    blockFileModel.setId(v[0]);
                    break;
                default:
                    break;
            }
        });
        return blockFileModel;
    }

    public String getId() {
        return id;
    }

    public BlockFileModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public BlockFileModel setDir(String dir) {
        this.dir = dir;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public BlockFileModel setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public long getFileSize() {
        return fileSize;
    }

    public BlockFileModel setFileSize(long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public BlockFileModel setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
        return this;
    }

    public int getChunks() {
        return chunks;
    }

    public BlockFileModel setChunks(int chunks) {
        this.chunks = chunks;
        return this;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public BlockFileModel setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
        return this;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public BlockFileModel setBlockSize(long blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    public MultipartFile getBlockFile() {
        return blockFile;
    }

    public BlockFileModel setBlockFile(MultipartFile blockFile) {
        this.blockFile = blockFile;
        return this;
    }

    public String getBlockMd5() {
        return blockMd5;
    }

    public BlockFileModel setBlockMd5(String blockMd5) {
        this.blockMd5 = blockMd5;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BlockFileModel.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("dir='" + dir + "'")
                .add("fileName='" + fileName + "'")
                .add("fileSize=" + fileSize)
                .add("fileMd5='" + fileMd5 + "'")
                .add("chunks=" + chunks)
                .add("chunkIndex=" + chunkIndex)
                .add("blockSize=" + blockSize)
                .add("blockFile=" + blockFile)
                .add("blockMd5='" + blockMd5 + "'")
                .toString();
    }
}
