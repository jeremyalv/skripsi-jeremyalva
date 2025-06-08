variable "aws_access_key" {
    type = string
    description = "AWS Access Key ID"
}

variable "aws_secret_key" {
    type = string
    description = "AWS Secret Access Key"
}

variable "public_key_path" {
    type = string
    description = <<DESCRIPTION
        Path to the SSH public key to be used for authentication.
        Ensure this keypair is added to your local SSH agent so provisioners can
        connect.

        Example: ~/.ssh/my_keys.pub
        Default: ~/.ssh/id_rsa.pub
    DESCRIPTION
}

variable "key_name_prefix" {
    type = string
    description = "The prefix for the randomly generated name for the AWS key pair to be used for SSH connections"
    default = "ecommerce-terraform-ssh-keys"
}

variable "region" {
    type = string
    description = "The AWS region in which the cluster will be deployed"
}

variable "availability_zone" {
    type = string
    description = "The AWS availability zone in which the cluster will run"
}

variable "availability_zone_failover" {
    type = string
    description = "The AWS availability zone for failover"
}

variable "aws_ami" {
    type = string
    description = "The AWS AMI to be used by the cluster instances"
}

variable "num_service_nodes" {
    type = number
    description = "The number of EC2 instances running application services"
}

variable "num_rds_nodes" {
    type = number
    description = "The number of RDS instances"
}

variable "num_elasticache_nodes" {
    type = number
    description = "The number of ElastiCache instances"
}

variable "num_prometheus_nodes" {
    type = number
    description = "The number of Prometheus server nodes"
}

variable "num_locust_nodes" {
    type = number
    description = "The number of Locust server nodes"
}

variable "instance_types" {
    type = map(string)
    description = "Key value pair of cluster components and their machine (EC2, db, etc) types"
}

variable "base_cidr_block" {
    type = string
    description = "The baseline CIDR block to be used by network assets for the cluster"
}

variable "ecommerce_allocated_storage" {
    type = number
    description = "Amount of GBs allocated for E-Commerce servers"
}

variable "prometheus_allocated_storage" {
    type = number
    description = "Amount of GBs allocated for Prometheus server"
}


variable "db_allocated_storage" {
    type = number
    description = "Amount of GBs allocated for DB storage"
}

variable "db_username" {
    type = string
    description = "The username of the database user"
}

variable "db_password" {
    type = string
    description = "The password of the database user"
}

variable "volume_device" {
    type = string
    description = "Volume to attach EBS disks"
}

variable "ecommerce_server_port" {
    type = number
    description = "Server port"
}

variable "ecommerce_actuator_port" {
    type = number
    description = "Actuator port"
}
