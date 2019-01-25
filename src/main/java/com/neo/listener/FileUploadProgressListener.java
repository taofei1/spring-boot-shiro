package com.neo.listener;

import com.neo.model.Progress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.ProgressListener;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
@Slf4j
public class FileUploadProgressListener implements ProgressListener {

    private HttpSession session;

    public void setSession(HttpSession session) {
        this.session = session;
        Progress status = new Progress();//保存上传状态
        session.setAttribute("status", status);
    }

    @Override
    public void update(long bytesRead, long contentLength, int items) {
        Progress status = (Progress) session.getAttribute("status");
        status.setBytesRead(bytesRead);
        status.setContentLength(contentLength);
        status.setItems(items);

    }

}