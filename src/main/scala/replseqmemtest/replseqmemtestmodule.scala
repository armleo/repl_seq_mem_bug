// See README.md for license details.

package test_pkg

import chisel3._

import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}



class sram_1rw_io(addr_width: Int, data_width: Int) extends Bundle {
	val address = Output(UInt(addr_width.W))
	val read = Output(Bool()) // Active High
	val read_data = Input(UInt(data_width.W))

	val write = Output(Bool()) // Active High
	val write_data = Output(UInt((data_width).W))
	
}


class sram_1rw(addr_width: Int, data_width: Int) extends Module {
	
	val io = IO(Flipped(new sram_1rw_io(addr_width, data_width)));
	val storage = SyncReadMem(1 << addr_width, UInt((data_width).W), SyncReadMem.ReadFirst);
	val read_reg = RegNext(io.read)
	val saved_data = RegNext(io.read_data)
	val read_data = storage.read(io.address, io.read)
	io.read_data := Mux(read_reg, read_data, saved_data)
	when(io.write) {
		storage.write(io.address, io.write_data)
	}
}


object sram_1rw_Driver extends App {
  (new ChiselStage).execute(Array("-frsq", "-m:ReplSeqMemTestModule:-o:generated_vlog/sram_1rw_mems","--target-dir", "generated_vlog"), Seq(ChiselGeneratorAnnotation(() => new sram_1rw(10, 32))))
}