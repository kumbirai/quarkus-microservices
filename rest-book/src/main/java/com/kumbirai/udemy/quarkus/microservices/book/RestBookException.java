package com.kumbirai.udemy.quarkus.microservices.book;

public class RestBookException extends Exception
{
	public RestBookException(Exception ex)
	{
		super(ex);
	}
}
