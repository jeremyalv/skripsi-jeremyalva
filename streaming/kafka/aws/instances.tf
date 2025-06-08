resource "aws_instance" "zookeeper" {
    ami = var.aws_ami
    instance_type = var.instance_types["zookeeper"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.kafka.id
    vpc_security_group_ids = [ aws_security_group.default.id, aws_security_group.zookeeper.id ]
    count = var.num_zookeeper_nodes

    tags = {
        Name = "zookeeper-${count.index + 1}"
    }
}

resource "aws_instance" "kafka_broker" {
    ami = var.aws_ami
    instance_type = var.instance_types["kafka_broker"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.kafka.id
    vpc_security_group_ids = [ aws_security_group.default.id, aws_security_group.kafka.id ]
    count = var.num_broker_nodes

    tags = {
        Name = "Kafka-Broker-${count.index + 1}"
    }
}

resource "aws_ebs_volume" "broker_volume" {
    count = var.num_broker_nodes
    size = var.broker_allocated_storage
    type = "gp3"
    availability_zone = aws_instance.kafka_broker[count.index].availability_zone

    tags = {
        Name = "Broker-volume-${count.index + 1}"
    }
}

resource "aws_volume_attachment" "broker_data_attachment" {
    count = var.num_broker_nodes
    volume_id = aws_ebs_volume.broker_volume[count.index].id
    instance_id = aws_instance.kafka_broker[count.index].id
    device_name = var.volume_device

    depends_on = [ aws_ebs_volume.broker_volume ]
}
