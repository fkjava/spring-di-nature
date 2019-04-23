package org.fkjava;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration("test")
@ComponentScan(basePackages = "org.fkjava.beans")
public class Config {
}
