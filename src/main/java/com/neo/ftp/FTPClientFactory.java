package com.neo.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Auther: 13965
 * @Date: 2018/11/1 16:16
 * @Description:
 * @Version: 1.0
 */
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {
    private static Logger log = LoggerFactory.getLogger(FTPClientFactory.class);
    private FTPPoolConfig ftpPoolConfig;

    public FTPPoolConfig getFtpPoolConfig() {
        return ftpPoolConfig;
    }

    public void setFtpPoolConfig(FTPPoolConfig ftpPoolConfig) {
        this.ftpPoolConfig = ftpPoolConfig;
    }

    /**
     * @Description: 新建对象
     * @param:
     * @return:
     * @auther: 13965
     * @date: 2018/11/1 16:21
     */
    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpPoolConfig.getConnectTimeOut());
        try {

            log.info("连接ftp服务器:" + ftpPoolConfig.getHost() + ":" + ftpPoolConfig.getPort());
            ftpClient.connect(ftpPoolConfig.getHost(), ftpPoolConfig.getPort());

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTPServer 拒绝连接");
                return null;
            }
            boolean result = ftpClient.login(ftpPoolConfig.getUsername(), ftpPoolConfig.getPassword());
            if (!result) {
                log.error("ftpClient登录失败!");
                throw new Exception("ftpClient登录失败! userName:" + ftpPoolConfig.getUsername() + ", password:" + ftpPoolConfig.getPassword());
            }

            ftpClient.setControlEncoding(ftpPoolConfig.getControlEncoding());
            ftpClient.setBufferSize(ftpPoolConfig.getBufferSize());
            ftpClient.setFileType(ftpPoolConfig.getFileType());
            ftpClient.setDataTimeout(ftpPoolConfig.getDataTimeout());
            ftpClient.setUseEPSVwithIPv4(ftpPoolConfig.isUseEPSVwithIPv4());
            if (ftpPoolConfig.isPassiveMode()) {
                log.info("进入ftp被动模式");
                ftpClient.enterLocalPassiveMode();//进入被动模式
            }
        } catch (IOException e) {
            log.error("FTP连接失败：", e);
        }
        return ftpClient;

    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<>(ftpClient);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        ftpClient.logout();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connect;
    }


    /**
     * No-op.
     *
     * @param p ignored
     */
    @Override
    public void activateObject(PooledObject<FTPClient> p) throws Exception {
        // The default implementation is a no-op.
    }

    /**
     * No-op.
     *
     * @param p ignored
     */
    @Override
    public void passivateObject(PooledObject<FTPClient> p) throws Exception {
    }
}
