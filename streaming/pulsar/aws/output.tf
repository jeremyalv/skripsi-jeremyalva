output "pulsar_broker_public_ips" {
    description = "Public IPs of the Pulsar broker instances"
    value = "${join(",", aws_instance.pulsar_broker.*.public_ip)}"
}

output "pulsar_broker_public_dns" {
    description = "Public DNS names of the Pulsar broker instances"
    value       = aws_instance.pulsar_broker[*].public_dns
}

output "zookeeper_private_ips" {
    description = "Private IPs of the Zookeeper instances"
    value       = aws_instance.zookeeper[*].private_ip
}

output "bookie_public_ips" {
    description = "Public IPs of the Bookie instances"
    value       = aws_instance.bookie[*].public_ip
}
