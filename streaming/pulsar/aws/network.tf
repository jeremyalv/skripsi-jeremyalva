resource "aws_vpc" "pulsar_vpc" {
    cidr_block = var.base_cidr_block
    enable_dns_support = true
    enable_dns_hostnames = true

    tags = {
        Name = "Pulsar-VPC"
    }
}

resource "aws_subnet" "default" {
    vpc_id = aws_vpc.pulsar_vpc.id
    cidr_block = cidrsubnet(var.base_cidr_block, 8, 2)
    availability_zone = var.availability_zone
    map_public_ip_on_launch = true

    tags = {
        Name = "Pulsar-Subnet"
    }
}

resource "aws_internet_gateway" "default" {
    vpc_id = aws_vpc.pulsar_vpc.id

    tags = {
        Name = "Pulsar-Internet-Gateway"
    }
}

# Route to Internet Gateway in Main Route Table
resource "aws_default_route_table" "main" {
    default_route_table_id = aws_vpc.pulsar_vpc.default_route_table_id

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.default.id
    }

    tags = {
        Name = "Pulsar-Main-RT-Simple"
    }
}
