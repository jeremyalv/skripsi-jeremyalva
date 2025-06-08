output "kafka_servers_local" {
    value = "${join(":9091,", aws_instance.kafka_broker.*.private_dns)}:9091"
}

output "kafka_broker_ips" {
    value = "${join(",", aws_instance.kafka_broker.*.public_ip)}"
}

output "kafka_instance_ids" {
    value       = "${aws_instance.kafka_broker.*.id}"
    description = "ID of kafka instances"
}

output "kafka_instance_count" {
    value       = "${var.num_broker_nodes}"
    description = "Count of kafka instances"
}
