package me.guillaumecle.aem.core.clientlibs;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import com.adobe.granite.ui.clientlibs.script.CompilerContext;
import com.adobe.granite.ui.clientlibs.script.ScriptCompiler;
import com.adobe.granite.ui.clientlibs.script.ScriptResource;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { ScriptCompiler.class })
public class RemoteScriptCompiler implements ScriptCompiler {

    private static final String COMPILER_EXTENSION = "href";
    private String MIME_TYPE = "";
    private String OUTPUT_EXTENSION = "";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void compile(final Collection<ScriptResource> scriptResources, final Writer out, final CompilerContext ctx)
            throws IOException {

        if (scriptResources != null && !scriptResources.isEmpty()) {
            scriptResources.stream().peek(first -> setReturnContext(first.getName())).forEach(resource -> {
                try {
                    out.write("/* Start: RemoteScriptCompiler Library */");
                    out.write("ScriptResource Name: " + resource.getName());
                    out.write("Compiler Context: " + ctx.getDestinationPath());
                    out.write("/* End: RemoteScriptCompiler Library */");
                } catch (final IOException e) {
                    writeError(out, resource.getName(), "Failed to compile library" + e.getMessage());
                }
            });
        }
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return COMPILER_EXTENSION;
    }

    @Override
    public String getOutputExtension() {
        return OUTPUT_EXTENSION;
    }

    @Override
    public boolean handles(final String extension) {
        return extension.endsWith(COMPILER_EXTENSION);
    }

    /**
     * A method to set return mime type and extension based on the libraries being
     * complied (JS/CSS).
     * 
     * @param resourceName
     */
    void setReturnContext(final String resourceName) {
        if (resourceName.endsWith(".css." + COMPILER_EXTENSION)) {
            MIME_TYPE = "text/css";
            OUTPUT_EXTENSION = "css";
        } else if (resourceName.endsWith(".js." + COMPILER_EXTENSION)) {
            MIME_TYPE = "application/javascript";
            OUTPUT_EXTENSION = "js";
        } else {
            MIME_TYPE = "text/plain";
            OUTPUT_EXTENSION = "txt";
        }
    }

    String getRemoteLibrary(final String libUrl) throws IOException {
        final HttpGet request = new HttpGet(libUrl);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            final int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == HttpStatus.SC_OK){
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return as a String
                    return EntityUtils.toString(entity);
                }
                else{
                    return "/* Empty Library Returned: [" + libUrl + "]*/";
                }
            }
            else {
                throw new HttpException("Invalid response code received [" + responseCode + "] from remote library URL [" + libUrl + "]");
            }
        }
    }

    void writeError(final Writer out, final String name, final String message) {

        final String n = System.lineSeparator();

        log.error("Failed to fetch or compile remote library {}: {}", name, message);

        try {
            out.write("/*****************************************************" + n);
            out.write("Fetching or compilation resulted in an error" + n);
            out.write("Input: " + name + n);
            out.write("Error: " + message + n);
            out.write("*****************************************************/" + n);
        } catch (final IOException e) {
            log.error("Failed to append error message to writer", e);
        }
    }

}