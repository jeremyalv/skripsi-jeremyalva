resource "aws_instance" "zookeeper" {
    ami = var.aws_ami
    instance_type = var.instance_types["zookeeper"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids = [ aws_security_group.default.id ]
    count = var.num_zookeeper_nodes

    iam_instance_profile = aws_iam_instance_profile.ec2_instance_profile.name
    
    tags = {
        Name = "Zookeeper-${count.index + 1}"
    }
}

resource "aws_instance" "bookie" {
    ami = var.aws_ami
    instance_type = var.instance_types["bookie"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids = [ aws_security_group.default.id ]
    count = var.num_bookie_nodes

    iam_instance_profile = aws_iam_instance_profile.ec2_instance_profile.name

    tags = {
        Name = "Bookie-${count.index + 1}"
    }
}

resource "aws_instance" "pulsar_broker" {
    ami                    = var.aws_ami
    instance_type          = var.instance_types["pulsar_broker"]
    key_name               = aws_key_pair.default.id
    subnet_id              = aws_subnet.default.id
    vpc_security_group_ids = [aws_security_group.default.id]
    count                  = var.num_broker_nodes

    iam_instance_profile = aws_iam_instance_profile.ec2_instance_profile.name

    tags = {
    Name = "Pulsar-Broker-${count.index + 1}"
    }
}

resource "aws_ebs_volume" "bookie_volume" {
    count = var.num_bookie_nodes
    size = var.broker_allocated_storage
    type = "gp3"
    availability_zone = aws_instance.bookie[count.index].availability_zone

    tags = {
        Name = "Broker-Volume-${count.index + 1}"
    }
}

resource "aws_volume_attachment" "broker_data_attachment" {
    count = var.num_bookie_nodes
    volume_id = aws_ebs_volume.bookie_volume[count.index].id
    instance_id = aws_instance.bookie[count.index].id
    device_name = var.volume_device

    depends_on = [ aws_ebs_volume.bookie_volume ]
}
