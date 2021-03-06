package com.googlecode.utterlyidle.jobs;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import java.util.Date;
import java.util.UUID;

import static com.googlecode.totallylazy.Option.applicate;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.time.Seconds.functions.between;

public interface Job {
    String status();

    UUID id();

    Request request();

    Option<Response> response();

    Date created();

    Option<Date> started();

    Option<Date> completed();

    class functions {
        public static Mapper<Job, UUID> id = new Mapper<Job, UUID>() {
            @Override
            public UUID call(final Job job) throws Exception {
                return job.id();
            }
        };

        public static Mapper<Job, Date> created = new Mapper<Job, Date>() {
            @Override
            public Date call(Job job) throws Exception {
                return job.created();
            }
        };

        public static Mapper<Job, Option<Date>> started = new Mapper<Job, Option<Date>>() {
            @Override
            public Option<Date> call(Job job) throws Exception {
                return job.started();
            }
        };

        public static Mapper<Job, Option<Date>> completed = new Mapper<Job, Option<Date>>() {
            @Override
            public Option<Date> call(Job job) throws Exception {
                return job.completed();
            }
        };

        public static Mapper<Job, Request> request = new Mapper<Job, Request>() {
            @Override
            public Request call(final Job job) throws Exception {
                return job.request();
            }
        };

        public static Mapper<Job, Option<Response>> response = new Mapper<Job, Option<Response>>() {
            @Override
            public Option<Response> call(final Job job) throws Exception {
                return job.response();
            }
        };
    }

    class methods {
        public static Option<Long> duration(Job job, Clock clock){
            return applicate(applicate(some(between), job.started()), some(job.completed().getOrElse(clock.now())));
        }
    }
}
