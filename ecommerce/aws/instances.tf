resource "aws_instance" "ecommerce" {
    ami = var.aws_ami
    instance_type = var.instance_types["ecommerce"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids = [ aws_security_group.default.id ]
    count = var.num_service_nodes
    availability_zone = var.availability_zone

    tags = {
        "Name" = "Ecommerce-Server-${count.index + 1}"
    }
}

resource "aws_instance" "prometheus" {
    ami = var.aws_ami
    instance_type = var.instance_types["prometheus"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids = [ aws_security_group.default.id ]
    availability_zone = var.availability_zone
    count = var.num_prometheus_nodes

    tags = {
        "Name" = "Prometheus-Server-${count.index + 1}"
    }
}

resource "aws_instance" "locust" {
    ami = var.aws_ami
    instance_type = var.instance_types["locust"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids =  [ aws_security_group.default.id ]
    availability_zone = var.availability_zone
    count = var.num_locust_nodes

    tags = {
        "Name" = "Locust-Server-${count.index + 1}"
    }
}

resource "aws_ebs_volume" "ecommerce_volume" {
    count = var.num_service_nodes
    size = var.ecommerce_allocated_storage
    type = "gp3"
    availability_zone = aws_instance.ecommerce[count.index].availability_zone

    tags = {
        Name = "Ecommerce-volume-${count.index + 1}"
    }
}

resource "aws_volume_attachment" "ecommerce_data_attachment" {
    count = var.num_service_nodes
    volume_id = aws_ebs_volume.ecommerce_volume[count.index].id
    instance_id = aws_instance.ecommerce[count.index].id
    device_name = var.volume_device

    depends_on = [ aws_ebs_volume.ecommerce_volume ]
}

resource "aws_lb" "ecommerce-alb" {
    name = "ecommerce-alb"
    internal = false
    ip_address_type = "ipv4"
    load_balancer_type = "application"
    security_groups =  [ aws_security_group.default.id ]
    subnets = [
        aws_subnet.default.id,
        aws_subnet.failover.id
    ]

    tags = {
        Name = "Ecommerce-ALB"
    }
}

resource "aws_lb_target_group" "ecommerce-target-group" {
    health_check {
        interval = 10
        path = "/actuator/health"
        protocol = "HTTP"
        port = var.ecommerce_actuator_port
        timeout = 6
        healthy_threshold = 3
        unhealthy_threshold = 2
    }

    name = "ecommerce-tg"
    port = var.ecommerce_server_port
    protocol = "HTTP"
    target_type = "instance"
    vpc_id = aws_vpc.ecommerce_vpc.id

    stickiness {
        enabled = true
        type = "lb_cookie"
        cookie_duration = 86400
    }

    lifecycle {
        create_before_destroy = true
    }
}

resource "aws_lb_listener" "alb-listener" {
    load_balancer_arn = aws_lb.ecommerce-alb.arn
    port = 80
    protocol = "HTTP"
    default_action {
        target_group_arn = aws_lb_target_group.ecommerce-target-group.arn
        type = "forward"
    }
}

resource "aws_lb_target_group_attachment" "ec2_attach" {
    count = length(aws_instance.ecommerce)
    target_group_arn = aws_lb_target_group.ecommerce-target-group.arn
    target_id = aws_instance.ecommerce[count.index].id
    port = var.ecommerce_server_port
}

resource "aws_ebs_volume" "prometheus_volume" {
    count = var.num_prometheus_nodes
    size = var.prometheus_allocated_storage
    type = "gp3"
    availability_zone = aws_instance.prometheus[count.index].availability_zone

    tags = {
        Name = "Prometheus-volume-${count.index + 1}"
    }
}

resource "aws_volume_attachment" "prometheus_data_attachment" {
    count = var.num_prometheus_nodes
    volume_id = aws_ebs_volume.prometheus_volume[count.index].id
    instance_id = aws_instance.prometheus[count.index].id
    device_name = var.volume_device

    depends_on = [ aws_ebs_volume.prometheus_volume ]
}

# NOTE: To create RDS via terraform you must create a DB Subnet Group
resource "aws_db_instance" "postgres" {
    allocated_storage = var.db_allocated_storage
    storage_type = "gp3"
    engine = "postgres"
    identifier = "postgres"
    instance_class = var.instance_types["rds"]
    username = var.db_username
    password = var.db_password

    db_subnet_group_name = aws_db_subnet_group.default.name
    vpc_security_group_ids = [ aws_security_group.default.id ]

    skip_final_snapshot = true

    tags = {
        Name = "Ecommerce-RDS"
    }
}
