//package io.hackfest;
//
//import javax.inject.Inject;
//import javax.enterprise.context.ApplicationScoped;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.eclipse.microprofile.reactive.messaging.Incoming;
//import io.smallrye.reactive.messaging.kafka.Record;
//
//import io.hackfest.dbmodel.DbPosUpdate;
//
//import javax.transaction.Transactional;
//
//import io.hackfest.PosUpdate;
//
//import io.micrometer.core.instrument.Tags;
//import io.micrometer.core.instrument.MeterRegistry;
//
//@ApplicationScoped
//public class StreamConsumer {
//	static final Logger LOGGER = LoggerFactory.getLogger(StreamConsumer.class);
//
//	@Inject
//	MeterRegistry registry;
//
//	@Inject
//	DatabaseTable tbl;
//
//	@Incoming("posupdates")
//	@Transactional
//	public void process(PosUpdate posupdate) {
//		registry.counter("posupdate_counter").increment();
//
//		var pos = new DbPosUpdate();
//		pos.UUID = posupdate.UUID;
//
//		tbl.persist(pos);
//
//
//		LOGGER.info("Got a PosUpdate!");
//	}
//}
