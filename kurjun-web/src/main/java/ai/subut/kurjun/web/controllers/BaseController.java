package ai.subut.kurjun.web.controllers;


import ai.subut.kurjun.web.filter.SecurityFilter;
import ninja.FilterWith;


@FilterWith( { SecurityFilter.class } )

public class BaseController
{

}
