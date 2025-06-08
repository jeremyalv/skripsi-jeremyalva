/* Default Security Groups */
resource "aws_security_group" "default" {
    vpc_id = aws_vpc.kafka_vpc.id

    # Allow SSH from anywhere
    ingress {
        from_port = 22
        to_port = 22
        protocol = "tcp"
        cidr_blocks = ["0.0.0.0/0"]
    }

    # Outbound internet access to anywhere
    egress {
        from_port = 0
        to_port = 0
        protocol = "-1"
        cidr_blocks = ["0.0.0.0/0"]
    }

    tags = {
        Name = "Default-Security-Group"
    }
}

/* ZooKeeper Security Groups */
resource "aws_security_group" "zookeeper" {
    name = "zookeeper-security-group"
    description = "Allow zookeeper traffic"
    vpc_id = aws_vpc.kafka_vpc.id

    tags = {
        Name = "zookeeper-security-group"
    }
}

resource "aws_security_group_rule" "zk_allow_kafka_ingress" {
    type = "ingress"
    from_port = 2181
    to_port = 2181
    protocol = "tcp"
    source_security_group_id = aws_security_group.kafka.id

    security_group_id = aws_security_group.zookeeper.id
}

resource "aws_security_group_rule" "zk_allow_peers_ingress" {
    type = "ingress"
    from_port = 2888
    to_port = 2888
    protocol = "tcp"
    self = true

    security_group_id = aws_security_group.zookeeper.id
}

resource "aws_security_group_rule" "zk_allow_leader_election" {
    type = "ingress"
    from_port = 3888
    to_port = 3888
    protocol = "tcp"
    self = true

    security_group_id = aws_security_group.zookeeper.id
}

/* Kafka Security Groups */
resource "aws_security_group" "kafka" {
    name = "kafka-security-group"
    description = "Allow Kafka traffic"
    vpc_id = aws_vpc.kafka_vpc.id

    tags = {
        Name = "kafka-security-group"
    }
}

resource "aws_security_group_rule" "kafka_allow_port_peers" {
    type = "ingress"
    from_port = 9091
    to_port = 9091
    protocol = "tcp"
    self = true

    security_group_id = aws_security_group.kafka.id
}

resource "aws_security_group_rule" "kafka_allow_port_external" {
    type = "ingress"
    from_port = 9092
    to_port = 9092
    protocol = "tcp"
    cidr_blocks = [ "0.0.0.0/0" ]

    security_group_id = aws_security_group.kafka.id
}

resource "aws_security_group_rule" "kafka_allow_promethus" {
    type = "ingress"
    from_port = 1234
    to_port = 1234
    protocol = "tcp"
    cidr_blocks = [ "0.0.0.0/0" ]

    security_group_id = aws_security_group.kafka.id
}
