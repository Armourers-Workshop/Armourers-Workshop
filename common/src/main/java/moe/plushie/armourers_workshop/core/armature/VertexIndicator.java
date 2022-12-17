package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.Lists;

import java.util.List;

public class VertexIndicator {
	@SuppressWarnings("unchecked")
	public static List<VertexIndicator> create(int[] drawingIndices, int[] vCounts, int[] animationIndices) {
		List<VertexIndicator> vertexIndicators = Lists.newArrayList();
		List<Integer>[] listedAnimationData = new List[vCounts.length];
		int indexPointer = 0;
		
		for (int i = 0; i < vCounts.length; i++) {
			int count = vCounts[i];
			List<Integer> list = Lists.newArrayList();
			
			for (int j = 0; j < count; j++) {
				list.add(animationIndices[indexPointer * 2]);
				list.add(animationIndices[indexPointer * 2 + 1]);
				indexPointer++;
			}
			
			listedAnimationData[i] = list;
		}
		
		for (int i = 0; i < drawingIndices.length / 3; i++) {
			int k = i * 3;
			int position = drawingIndices[k];
			int uv = drawingIndices[k + 1];
			int normal = drawingIndices[k + 2];
			VertexIndicator vi = new VertexIndicator(position, uv, normal);
			List<Integer> list = listedAnimationData[position];
			
			for (int j = 0; j < list.size() / 2; j++) {
				vi.addAnimationData(list.get(j * 2), list.get(j * 2 + 1));
			}
			
			vertexIndicators.add(vi);
		}
		
		return vertexIndicators;
	}
	
	public final int position;
	public final int uv;
	public final int normal;
	public final List<Integer> joint;
	public final List<Integer> weight;
	
	public VertexIndicator(int position, int uv, int normal) {
		this.position = position;
		this.uv = uv;
		this.normal = normal;
		this.joint = Lists.newArrayList();
		this.weight = Lists.newArrayList();
	}
	
	public void addAnimationData(int jointId, int weight) {
		this.joint.add(jointId);
		this.weight.add(weight);
	}
}
