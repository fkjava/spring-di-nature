package fkjava;

import fkjava.annotations.ComponentScan;
import fkjava.annotations.Configuration;

@Configuration("test")
@ComponentScan(basePackages = "org.fkjava.beans")
public class Config {
}
