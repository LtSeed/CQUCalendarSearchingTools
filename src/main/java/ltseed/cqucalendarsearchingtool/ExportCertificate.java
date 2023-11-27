package ltseed.cqucalendarsearchingtool;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.mitm.CertificateAndKeySource;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class ExportCertificate {
    public static void main(String[] args) {
        // 创建Root CA证书生成器
        CertificateAndKeySource source = RootCertificateGenerator.builder().build();

        // 创建并启动BrowserMob Proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.setMitmManager(ImpersonatingMitmManager.builder().rootCertificateSource(source).build());
        proxy.start(0);

        // 获取根CA证书
        X509Certificate caCertificate = source.load().getCertificate();

        // 将证书保存为文件
        try (FileOutputStream fos = new FileOutputStream("E:\\SERVER\\browsermob-proxy.cer")) {
            fos.write(caCertificate.getEncoded());
        } catch (IOException | CertificateEncodingException e) {
            e.printStackTrace();
        }

        // 停止Proxy
        proxy.stop();
    }
}
