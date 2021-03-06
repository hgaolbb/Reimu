package com.github.hgaol.reimu.instructions.references;

import com.github.hgaol.reimu.instructions.base.ClassInitLogic;
import com.github.hgaol.reimu.instructions.base.Index16Instruction;
import com.github.hgaol.reimu.rtda.Frame;
import com.github.hgaol.reimu.rtda.heap.CpInfos;
import com.github.hgaol.reimu.rtda.heap.ReClass;
import com.github.hgaol.reimu.rtda.heap.RtConstantPool;
import com.github.hgaol.reimu.util.MethodUtils;

/**
 * Invoke a class (static) method
 * @author Gao Han
 * @date: 2018年04月25日
 */
public class InvokeStatic extends Index16Instruction {

  @Override
  public void execute(Frame frame) {
    RtConstantPool cp = frame.getMethod().getClazz().getConstantPool();
    CpInfos.MethodRef methodRef = (CpInfos.MethodRef) cp.getConstant(index);
    ReClass.Method resolvedMethod = methodRef.resolvedMethod();

    if (!resolvedMethod.isStatic()) {
      throw new Error("java.lang.IncompatibleClassChangeError");
    }

    ReClass clazz = resolvedMethod.getClazz();
    if (!clazz.isInitStarted()) {
      frame.revertNextPc();
      ClassInitLogic.initClass(frame.getThread(), clazz);
      return;
    }

    MethodUtils.invokeMethod(frame, resolvedMethod);
  }

}
