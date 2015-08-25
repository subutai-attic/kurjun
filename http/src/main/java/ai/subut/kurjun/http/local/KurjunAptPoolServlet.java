package ai.subut.kurjun.http.local;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.http.HttpServletBase;
import ai.subut.kurjun.model.repository.LocalRepository;


@Singleton
class KurjunAptPoolServlet extends HttpServletBase
{
    private LocalRepository repository;


    @Inject
    public KurjunAptPoolServlet( LocalRepository repository )
    {
        this.repository = repository;
    }


    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        // TODO:
    }


}

