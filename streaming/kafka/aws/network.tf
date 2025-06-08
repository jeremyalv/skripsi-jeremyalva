resource "aws_vpc" "kafka_vpc" {
    cidr_block = var.base_cidr_block
    enable_dns_support = true
    enable_dns_hostnames = true

    tags = {
        Name = "Kafka-VPC"
    }
}

resource "aws_subnet" "kafka" {
    vpc_id = aws_vpc.kafka_vpc.id
    cidr_block = cidrsubnet(var.base_cidr_block, 8, 2)
    availability_zone = var.availability_zone
    map_public_ip_on_launch = true

    tags = {
        Name = "kafka-public-subnet"
    }
}

resource "aws_route_table" "kafka_rt" {
    vpc_id = aws_vpc.kafka_vpc.id

    tags = {
        Name = "Kafka-Route-Table"
    }
}

resource "aws_route" "kafka_public" {
    route_table_id = aws_route_table.kafka_rt.id
    destination_cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
}

resource "aws_route_table_association" "kafka_public" {
    subnet_id = aws_subnet.kafka.id
    route_table_id = aws_route_table.kafka_rt.id
}

resource "aws_internet_gateway" "igw" {
    vpc_id = aws_vpc.kafka_vpc.id

    tags = {
        Name = "Kafka-Internet-Gateway"
    }
}
