package ai.subut.kurjun.http.local;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import ai.subut.kurjun.model.repository.LocalRepository;


class KurjunAptPoolServlet extends HttpServlet
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

