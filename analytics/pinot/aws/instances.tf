resource "aws_instance" "pinot" {
    ami = var.aws_ami
    instance_type = var.instance_types["pinot"]
    key_name = aws_key_pair.default.id
    subnet_id = aws_subnet.default.id
    vpc_security_group_ids = [ aws_security_group.default.id ]
    count = var.num_pinot_nodes

    tags = {
        "Name" = "Pinot-${count.index + 1}"
    }
}
