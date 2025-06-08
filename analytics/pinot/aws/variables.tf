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
    default = "pinot-terraform-ssh-keys"
}

variable "region" {
    type = string
    description = "The AWS region in which the cluster will be deployed"
}

variable "availability_zone" {
    type = string
    description = "The AWS availability zone in which the cluster will run"
}

variable "aws_ami" {
    type = string
    description = "The AWS AMI to be used by the cluster instances"
}

variable "num_pinot_nodes" {
    type = number
    description = "The number of EC2 instances running Pinot all-in-one servers"
}

variable "instance_types" {
    type = map(string)
    description = "Key value pair of cluster components and their EC2 types"
}

variable "base_cidr_block" {
    type = string
    description = "The baseline CIDR block to be used by network assets for the cluster"
}
