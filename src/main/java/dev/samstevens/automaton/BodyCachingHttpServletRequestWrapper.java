package dev.samstevens.automaton;

import org.apache.commons.io.IOUtils;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class BodyCachingHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    BodyCachingHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        try {
            body = IOUtils.toByteArray(super.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Could not read request InputStream.", e);
        }

        return new ServletInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }
}