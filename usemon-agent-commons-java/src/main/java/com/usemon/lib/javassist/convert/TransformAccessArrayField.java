/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999-2007 Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */package com.usemon.lib.javassist.convert;
import com.usemon.lib.javassist.CtClass;import com.usemon.lib.javassist.NotFoundException;import com.usemon.lib.javassist.CodeConverter.ArrayAccessReplacementMethodNames;import com.usemon.lib.javassist.bytecode.BadBytecode;import com.usemon.lib.javassist.bytecode.CodeIterator;import com.usemon.lib.javassist.bytecode.ConstPool;

/**
 *  
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.4 $ 
 */
public class TransformAccessArrayField extends Transformer {
// CtClass componentType;

   String methodClassname;
   ArrayAccessReplacementMethodNames names;

   public TransformAccessArrayField(Transformer next, String methodClassname,
                                    ArrayAccessReplacementMethodNames names)
       throws NotFoundException
   {
       super(next);
       this.methodClassname = methodClassname;
       this.names = names;
   }

   public int transform(CtClass tclazz, int pos, CodeIterator iterator,
                        ConstPool cp) throws BadBytecode
   {
      int c = iterator.byteAt(pos);
      
      if (c == AALOAD || c == BALOAD || c == CALOAD || c == DALOAD
          || c == FALOAD || c == IALOAD || c == LALOAD || c == SALOAD)
         replace(cp, iterator, pos, c, getLoadReplacementSignature(c));
      else if (c == AASTORE || c == BASTORE || c == CASTORE || c == DASTORE
               || c == FASTORE || c == IASTORE || c == LASTORE || c == SASTORE)
         replace(cp, iterator, pos, c, getStoreReplacementSignature(c));

      return pos;
   }
   
   private void replace(ConstPool cp, CodeIterator iterator,
                        int pos, int opcode, String signature)
       throws BadBytecode
   {
      String methodName = getMethodName(opcode);
      if (methodName != null) {
         iterator.insertGap(2);
         int mi = cp.addClassInfo(methodClassname);
         int methodref = cp.addMethodrefInfo(mi, methodName, signature);
         iterator.writeByte(INVOKESTATIC, pos);
         iterator.write16bit(methodref, pos + 1);
      }
   }

   private String getMethodName(int opcode) {
        String methodName = null;
        switch (opcode) {
        case AALOAD:
            methodName = names.objectRead();
            break;
        case BALOAD:
            methodName = names.byteOrBooleanRead();
            break;
        case CALOAD:
            methodName = names.charRead();
            break;
        case DALOAD:
            methodName = names.doubleRead();
            break;
        case FALOAD:
            methodName = names.floatRead();
            break;
        case IALOAD:
            methodName = names.intRead();
            break;
        case SALOAD:
            methodName = names.shortRead();
            break;
        case LALOAD:
            methodName = names.longRead();
            break;
        case AASTORE:
            methodName = names.objectWrite();
            break;
        case BASTORE:
            methodName = names.byteOrBooleanWrite();
            break;
        case CASTORE:
            methodName = names.charWrite();
            break;
        case DASTORE:
            methodName = names.doubleWrite();
            break;
        case FASTORE:
            methodName = names.floatWrite();
            break;
        case IASTORE:
            methodName = names.intWrite();
            break;
        case SASTORE:
            methodName = names.shortWrite();
            break;
        case LASTORE:
            methodName = names.longWrite();
            break;
        }

        if (methodName.equals(""))
            methodName = null;

        return methodName;
    }

   private String getLoadReplacementSignature(int opcode)
       throws BadBytecode
   {
        switch (opcode) {
        case AALOAD:
            return "(Ljava/lang/Object;I)Ljava/lang/Object;";
        case BALOAD:
            return "(Ljava/lang/Object;I)B";
        case CALOAD:
            return "(Ljava/lang/Object;I)C";
        case DALOAD:
            return "(Ljava/lang/Object;I)D";
        case FALOAD:
            return "(Ljava/lang/Object;I)F";
        case IALOAD:
            return "(Ljava/lang/Object;I)I";
        case SALOAD:
            return "(Ljava/lang/Object;I)S";
        case LALOAD:
            return "(Ljava/lang/Object;I)J";
        }

        throw new BadBytecode(opcode);
    }
   
   private String getStoreReplacementSignature(int opcode)
       throws BadBytecode
   {
        switch (opcode) {
        case AASTORE:
            return "(Ljava/lang/Object;ILjava/lang/Object;)V";
        case BASTORE:
            return "(Ljava/lang/Object;IB)V";
        case CASTORE:
            return "(Ljava/lang/Object;IC)V";
        case DASTORE:
            return "(Ljava/lang/Object;ID)V";
        case FASTORE:
            return "(Ljava/lang/Object;IF)V";
        case IASTORE:
            return "(Ljava/lang/Object;II)V";
        case SASTORE:
            return "(Ljava/lang/Object;IS)V";
        case LASTORE:
            return "(Ljava/lang/Object;IJ)V";
        }

        throw new BadBytecode(opcode);
    }
}
