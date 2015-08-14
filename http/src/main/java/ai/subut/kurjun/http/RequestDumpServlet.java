package ai.subut.kurjun.http;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;


@Singleton
public class RequestDumpServlet extends HttpServlet
{


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        try ( ServletOutputStream os = resp.getOutputStream() )
        {
            dumpRequestData( os, req );
        }
    }


    private void dumpRequestData( ServletOutputStream out, HttpServletRequest req ) throws IOException
    {
        out.println( "Char encoding   : " + req.getCharacterEncoding() );
        out.println( "Content type    : " + req.getContentType() );
        out.println( "Context path    : " + req.getContextPath() );
        out.println( "Local addr      : " + req.getLocalAddr() );
        out.println( "Local name      : " + req.getLocalName() );
        out.println( "Method          : " + req.getMethod() );
        out.println( "Path info       : " + req.getPathInfo() );
        out.println( "Path translated : " + req.getPathTranslated() );
        out.println( "Protocol        : " + req.getProtocol() );
        out.println( "Query string    : " + req.getQueryString() );
        out.println( "Remote addr     : " + req.getRemoteAddr() );
        out.println( "Remote host     : " + req.getRemoteHost() );
        out.println( "Remote user     : " + req.getRemoteUser() );
        out.println( "Request URI     : " + req.getRequestURI() );
        out.println( "Scheme          : " + req.getScheme() );
        out.println( "Server name     : " + req.getServerName() );
        out.println( "Servlet path    : " + req.getServletPath() );
    }


}

