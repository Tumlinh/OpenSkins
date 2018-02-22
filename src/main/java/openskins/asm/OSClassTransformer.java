package openskins.asm;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.commons.codec.digest.DigestUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class OSClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		// Check whether the loading class has to be patched
		for (int i = 0; i < OSFMLLoadingPlugin.patchedClasses.size(); i++) {
			PatchedClass patchedClass = OSFMLLoadingPlugin.patchedClasses.get(i);
			if (patchedClass.name.equals(transformedName))
				basicClass = patchClass(basicClass, patchedClass);
		}
		
		return basicClass;
	}
	
	public byte[] patchClass(byte[] basicClass, PatchedClass patchedClass)
	{
		byte[] newClass = basicClass; // The actual patch

		// Visit the basic class
		//System.out.println("********* Visiting class " + patchedClass.nice_name + " (" + patchedClass.name_obf + ")");
		//System.out.println(DigestUtils.md5Hex(basicClass));
		
		// Check class integrity
		if (!(DigestUtils.md5Hex(basicClass).equals(patchedClass.hash)))
			System.out.println("Warning: class " + patchedClass.nice_name + " has been patched by another mod. Bugs may occur");
		
		ClassNode basicClassNode = null;
		basicClassNode = new ClassNode();
		ClassReader basicClassReader = new ClassReader(basicClass);
		basicClassReader.accept(basicClassNode, 0);
		Iterator<MethodNode> basicMethods = basicClassNode.methods.iterator();
		
		// Enumerate inner methods
		while (basicMethods.hasNext()) {
			MethodNode basicMethodNode = basicMethods.next();
			//System.out.println("<<<<<<<<" + basicMethodNode.name + " " + basicMethodNode.desc);
			
			// Loop through patched methods
			for (int i = 0; i < patchedClass.methods.size(); i++) {
				PatchedMethod patchedMethod = patchedClass.methods.get(i);
				// Found the method to patch
				if (basicMethodNode.name.equals(patchedMethod.name_obf) && basicMethodNode.desc.equals(patchedMethod.desc)) {
					basicMethodNode.instructions = patchedMethod.instructions;
					patchedClass.methods.remove(i);
					System.out.println("Method " + patchedMethod.name + " (" + patchedMethod.name_obf + ") " + patchedMethod.desc + " patched successfully");
					break;
				}
			}
			
			// Exit when there are no methods left to patch
			if (patchedClass.methods.isEmpty()) {
				// Generate class bytecode
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				basicClassNode.accept(writer);
				newClass = writer.toByteArray();

				// Remove class from list (improve performance)
				OSFMLLoadingPlugin.patchedClasses.remove(patchedClass);
				
				System.out.println("Class " + patchedClass.nice_name + " (" + patchedClass.name_obf + ") patched successfully");
				break;
			}
		}
		
		return newClass;
	}
}